#!/usr/bin/env bash
# mirror-packages.sh — LECTURER-ONLY maintenance script (bash, deliberately no
# .ps1 twin: students never run this).
#
# Copies the Helex Maven artifacts this template depends on from the private
# helex-solutions registries into THIS repository's own Maven registry
# (maven.pkg.github.com/igorboss/ite4120). Why: GitHub's Maven registry has no
# per-package access — reading a package requires read access to its source
# REPOSITORY. Mirroring into the course repo means the one grant students
# already have (access to this repo, for cloning) also serves the packages,
# with no helex-solutions seats or source exposure. The npm side needs no
# mirror: the four @helex-solutions npm packages are public per-package.
#
# Usage:
#   scripts/mirror-packages.sh                 # mirror the LATEST versions
#   scripts/mirror-packages.sh 0.1.39 0.25.0   # mirror exact versions
#
# Then bump backend/gradle.properties to the mirrored versions.
#
# Auth: GITHUB_GOD_TOKEN — a token that can read helex-solutions packages and
# write packages on igorboss/ite4120 (scopes: read:packages, write:packages,
# repo). Falls back to GITHUB_TOKEN. Republishing an existing version is
# detected and skipped, never overwritten.
set -euo pipefail

[ -n "${GITHUB_GOD_TOKEN:-}" ] || [ ! -f ~/.zprofile ] || source ~/.zprofile
TOKEN="${GITHUB_GOD_TOKEN:-${GITHUB_TOKEN:-}}"
[ -n "$TOKEN" ] || { echo "set GITHUB_GOD_TOKEN (read helex packages + write:packages on the course repo)" >&2; exit 1; }
ACTOR="${GITHUB_ACTOR:-igorboss}"

SRC_ORG=helex-solutions
DST_REPO=igorboss/ite4120

COMMONS_VERSION="${1:-$(gh api "/orgs/$SRC_ORG/packages/maven/org.helex.emr.commons-db/versions" --jq '.[0].name')}"
FORGE_VERSION="${2:-$(gh api "/orgs/$SRC_ORG/packages/maven/org.helex.forge.forge-xroad/versions" --jq '.[0].name')}"
echo "mirroring org.helex.emr:* $COMMONS_VERSION and org.helex.forge:forge-xroad $FORGE_VERSION -> $DST_REPO"

mirror() { # source-repo group artifact version
  local repo=$1 group=$2 artifact=$3 version=$4
  local gpath="${group//.//}"
  local src="https://maven.pkg.github.com/$SRC_ORG/$repo/$gpath/$artifact/$version"
  local dst="https://maven.pkg.github.com/$DST_REPO/$gpath/$artifact/$version"

  echo "$group:$artifact:$version"
  if curl -sf -u "$ACTOR:$TOKEN" -o /dev/null "$dst/$artifact-$version.pom"; then
    echo "  already mirrored — skipped (existing versions are never overwritten)"
    return
  fi

  local copied=0 tmp code
  for base in "$artifact-$version.pom" "$artifact-$version.jar" \
              "$artifact-$version.module" "$artifact-$version-sources.jar"; do
    for f in "$base" "$base.sha1" "$base.md5" "$base.sha256" "$base.sha512"; do
      tmp=$(mktemp)
      if curl -sf -u "$ACTOR:$TOKEN" -o "$tmp" "$src/$f"; then
        code=$(curl -s -u "$ACTOR:$TOKEN" -X PUT --data-binary @"$tmp" -o /dev/null -w '%{http_code}' "$dst/$f")
        case "$code" in
          2*) echo "  $f"; copied=$((copied+1)) ;;
          *)  echo "  $f — upload FAILED (HTTP $code)" >&2; rm -f "$tmp"; exit 1 ;;
        esac
      fi
      rm -f "$tmp"
    done
  done
  [ "$copied" -gt 0 ] || { echo "  found nothing at the source — wrong version?" >&2; exit 1; }
  echo "  -> $copied files"
}

for a in commons-db commons-db-core commons-model commons-util; do
  mirror emr-repo org.helex.emr "$a" "$COMMONS_VERSION"
done
mirror forge org.helex.forge forge-xroad "$FORGE_VERSION"

echo "done. Now set helexCommonsVersion=$COMMONS_VERSION and forgeVersion=$FORGE_VERSION in backend/gradle.properties and run the backend tests."
