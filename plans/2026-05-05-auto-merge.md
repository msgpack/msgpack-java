# Add auto-merge GitHub Actions workflow

## Goal

Add a workflow that automatically enables auto-merge on PRs from trusted bots
(Dependabot and Scala Steward) for non-major version bumps, mirroring the
pattern used in `wvlet/uni/.github/workflows/auto-merge.yml`.

## Context

- Recent PR history shows the repo regularly receives many bot PRs:
  - `dependabot[bot]` — for GitHub Actions version bumps
  - `scala-steward` — for Scala/sbt/Java library updates
- These currently require manual merge from the maintainer.
- Repo settings already allow auto-merge (`allow_auto_merge: true`) and squash
  is the default merge method.
- CI passes branch-protection checks on PRs; once a PR is approved and CI is
  green, GitHub will merge it automatically.

## Differences from wvlet/uni

- **No GitHub App token.** wvlet/uni uses a GitHub App (`APP_ID` +
  `APP_PRIVATE_KEY`) to bypass the read-only `GITHUB_TOKEN` that GitHub
  hands to `pull_request` workflows triggered by Dependabot. msgpack/msgpack-java
  doesn't have that App configured, so this workflow uses
  `on: pull_request_target` instead — that event runs in the base-branch
  context where the workflow's declared `permissions:` block actually grants
  write access to `GITHUB_TOKEN`. We never check out PR head code, so the
  usual `pull_request_target` injection risk does not apply.
- **Scala Steward actor is `scala-steward`**, not wvlet/uni's
  `scala-steward-wvlet[bot]` or `xerial-bot` (visible in `gh pr list`).
- **Filter on `github.event.pull_request.user.login`**, not `github.actor`,
  because under `pull_request_target` the latter can resolve to the merger
  rather than the PR author.

## Plan

1. Add `.github/workflows/auto-merge.yml` with two jobs:
   - **auto-merge-dependabot**: triggers when `github.actor == 'dependabot[bot]'`,
     uses `dependabot/fetch-metadata@v3` to read the update type, and runs
     `gh pr merge --squash --auto` only when the update is **not**
     `version-update:semver-major`.
   - **auto-merge-scala-steward**: triggers when `github.actor == 'scala-steward'`
     and runs `gh pr merge --squash --auto` for all such PRs (Scala Steward
     does not surface a structured "major" signal the way Dependabot's
     fetch-metadata action does, so we rely on Scala Steward's own
     `pullRequests.allowedUpdates` config — already filtered upstream — and
     skip if the PR has a `semver-major` label).
2. Set workflow-level `permissions` to the minimum required:
   `contents: write` and `pull-requests: write`.
3. Use `GITHUB_TOKEN` directly via `env: GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}`.

## Out of scope

- Setting up a GitHub App for elevated bot identity.
- Auto-approving PRs (a human approval may still be required by branch
  protection — auto-merge will simply wait for it).
- Changing branch protection rules.

## Validation

- Lint the YAML by checking the file parses (visual review + actionlint if
  available).
- After merge, watch the next dependabot/scala-steward PR to confirm
  auto-merge gets enabled.
