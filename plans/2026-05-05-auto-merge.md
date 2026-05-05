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

1. Add `.github/workflows/auto-merge.yml` with one job:
   - **auto-merge-dependabot**: triggers when
     `github.event.pull_request.user.login == 'dependabot[bot]'`, uses
     `dependabot/fetch-metadata@v2` to read the update type, and runs
     `gh pr merge --squash --auto` only when the update is **not**
     `version-update:semver-major`.
2. Set workflow-level `permissions` to the minimum required:
   `contents: write` and `pull-requests: write`.
3. Use `GITHUB_TOKEN` directly via `env: GH_TOKEN: ${{ secrets.GITHUB_TOKEN }}`.

### Scala Steward (deferred)

The initial draft also auto-merged Scala Steward PRs, gated on a
`semver-major` label being absent. But this repo's existing Scala Steward PRs
only carry `library-update` (and sometimes `internal`) — no semver labels are
configured, so a `!contains(..., 'semver-major')` guard is effectively a
no-op and would auto-merge every Scala Steward PR including major bumps.
Codex review caught this. Rather than ship an unsafe guard, we drop the
Scala Steward job from this PR. Re-adding it should be a follow-up that
either:
- Defines `early-semver-major`/`early-semver-minor`/`early-semver-patch`
  labels in the repo (Scala Steward only applies labels that already exist)
  and adds a `.scala-steward.conf` enabling them, then guards on
  `early-semver-major`.
- Or uses a manual opt-in label like `auto-merge` that the maintainer adds
  after a quick review.

## Out of scope

- Setting up a GitHub App for elevated bot identity.
- Auto-approving PRs (a human approval may still be required by branch
  protection — auto-merge will simply wait for it).
- Changing branch protection rules.
- Scala Steward auto-merge (see above).

## Validation

- Lint the YAML by checking the file parses (visual review + actionlint if
  available).
- After merge, watch the next dependabot/scala-steward PR to confirm
  auto-merge gets enabled.
