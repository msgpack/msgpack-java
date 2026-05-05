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
   - **auto-merge-dependabot**: triggers when
     `github.event.pull_request.user.login == 'dependabot[bot]'`, uses
     `dependabot/fetch-metadata@v2` to read the update type, and runs
     `gh pr merge --squash --auto` only when the update is **not**
     `version-update:semver-major`.
   - **auto-merge-scala-steward**: triggers when
     `github.event.pull_request.user.login == 'scala-steward'` and
     auto-merges unless the PR carries a `semver-major` or
     `early-semver-major` label.
2. Set workflow-level `permissions` to the minimum required:
   `contents: write` and `pull-requests: write`.
3. Use `GITHUB_TOKEN` directly via `env: GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}`.

### Scala Steward label caveat

This repo's current Scala Steward PRs only carry `library-update` (and
sometimes `internal`) — no semver labels are configured upstream, so the
`semver-major` / `early-semver-major` guard is effectively a no-op until:
- The labels are added to the repo (Scala Steward only applies labels that
  already exist), and
- Scala Steward is configured (e.g. via `.scala-steward.conf`) to attach
  them.

This matches the same caveat in the wvlet/uni reference implementation,
which uses `semver-spec-major` against `github.event.issue.labels` (not even
the right field on a PR event) — so its guard is also effectively a no-op
in practice. We accept the same trade-off here: most Scala Steward PRs are
patch/minor library updates, CI runs across JDK 8/11/17/21/24 and will fail
the merge-readiness checks if anything regresses, and a major bump that
slips through can be reverted. A follow-up could tighten this by setting up
proper semver labels.

## Out of scope

- Setting up a GitHub App for elevated bot identity.
- Auto-approving PRs (a human approval may still be required by branch
  protection — auto-merge will simply wait for it).
- Changing branch protection rules.
- Configuring Scala Steward to apply semver labels (see caveat above).

## Validation

- Lint the YAML by checking the file parses (visual review + actionlint if
  available).
- After merge, watch the next dependabot/scala-steward PR to confirm
  auto-merge gets enabled.
