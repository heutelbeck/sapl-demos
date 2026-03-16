#!/usr/bin/env bash
set -euo pipefail

#
# Testing SAPL Policies with the SAPL Node CLI
# ==============================================
#
# The SAPL Node CLI includes a 'test' command that discovers .sapl policy files
# and .sapltest test files, executes all test scenarios, and generates coverage
# reports -- without requiring Maven, Gradle, or any JVM build tool.
#
# This makes it possible to test SAPL policies in CI/CD pipelines, editor
# integrations, or any environment where the 'sapl' binary is available.
#
# The 'sapl test' command supports:
#
#   --dir <path>          Directory containing .sapl policy files
#   --testdir <path>      Directory containing .sapltest test files
#                         (defaults to --dir if not specified)
#   --output <path>       Output directory for coverage data and reports
#   --html / --no-html    Enable/disable HTML coverage report (default: on)
#   --sonar / --no-sonar  Enable/disable SonarQube coverage report
#
# Quality gate thresholds (0-100, 0 = disabled):
#
#   --policy-set-hit-ratio <n>     Required policy set hit ratio
#   --policy-hit-ratio <n>         Required policy hit ratio
#   --condition-hit-ratio <n>      Required condition hit ratio
#   --branch-coverage-ratio <n>    Required branch coverage ratio
#
# Exit codes:
#
#   0  All tests passed (and quality gate met, if configured)
#   1  Error during test execution (I/O, parse errors)
#   2  One or more tests failed
#   3  Quality gate not met (tests passed but coverage below threshold)
#
# Installation:
#
#   The 'sapl' binary is available as a native executable for Linux, macOS,
#   and Windows. See https://sapl.io for installation instructions.
#
# This script runs all demo project tests. Configure the binary location via:
#
#   ./run-sapl-tests.sh                          # 'sapl' on PATH
#   ./run-sapl-tests.sh --sapl /path/to/sapl     # explicit path
#   SAPL=/path/to/sapl ./run-sapl-tests.sh       # environment variable
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SAPL="${SAPL:-sapl}"

while [[ $# -gt 0 ]]; do
    case "$1" in
        --sapl) SAPL="$2"; shift 2 ;;
        *) echo "Unknown option: $1"; exit 1 ;;
    esac
done

failures=0
total=0

run_tests() {
    local name="$1" dir="$2" testdir="$3" output="$4"
    shift 4

    total=$((total + 1))
    echo "--- ${name} ---"

    if "${SAPL}" test --dir "${dir}" --testdir "${testdir}" --output "${output}" "$@"; then
        echo "PASSED"
    else
        local rc=$?
        echo "FAILED (exit ${rc})"
        failures=$((failures + 1))
    fi
    echo
}

# test-dsl-junit: all tests (unit + integration in single pass, matching Maven behavior)
run_tests "test-dsl-junit" \
    "${SCRIPT_DIR}/test-dsl-junit/src/main/resources" \
    "${SCRIPT_DIR}/test-dsl-junit/src/test/resources" \
    "${SCRIPT_DIR}/test-dsl-junit/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --policy-hit-ratio 100 --condition-hit-ratio 70

# test-dsl-programmatic: unit tests
run_tests "test-dsl-programmatic (unit)" \
    "${SCRIPT_DIR}/test-dsl-programmatic/src/main/resources/policies" \
    "${SCRIPT_DIR}/test-dsl-programmatic/src/test/resources/unit" \
    "${SCRIPT_DIR}/test-dsl-programmatic/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --policy-hit-ratio 100 --condition-hit-ratio 50

# webflux
run_tests "webflux" \
    "${SCRIPT_DIR}/webflux/src/main/resources/policies" \
    "${SCRIPT_DIR}/webflux/src/test/resources/unit" \
    "${SCRIPT_DIR}/webflux/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --policy-hit-ratio 100 --condition-hit-ratio 95

# web-mvc-app
run_tests "web-mvc-app" \
    "${SCRIPT_DIR}/web-mvc-app/src/main/resources/policies" \
    "${SCRIPT_DIR}/web-mvc-app/src/test/resources/unit" \
    "${SCRIPT_DIR}/web-mvc-app/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --condition-hit-ratio 70

# queryrewriting-jpa
run_tests "queryrewriting-jpa" \
    "${SCRIPT_DIR}/queryrewriting-jpa/src/main/resources/policies" \
    "${SCRIPT_DIR}/queryrewriting-jpa/src/test/resources/unit" \
    "${SCRIPT_DIR}/queryrewriting-jpa/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --condition-hit-ratio 100

# queryrewriting-sql-reactive
run_tests "queryrewriting-sql-reactive" \
    "${SCRIPT_DIR}/queryrewriting-sql-reactive/src/main/resources/policies" \
    "${SCRIPT_DIR}/queryrewriting-sql-reactive/src/test/resources/unit" \
    "${SCRIPT_DIR}/queryrewriting-sql-reactive/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --condition-hit-ratio 100

# queryrewriting-mongodb-reactive
run_tests "queryrewriting-mongodb-reactive" \
    "${SCRIPT_DIR}/queryrewriting-mongodb-reactive/src/main/resources/policies" \
    "${SCRIPT_DIR}/queryrewriting-mongodb-reactive/src/test/resources/unit" \
    "${SCRIPT_DIR}/queryrewriting-mongodb-reactive/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --condition-hit-ratio 100

# webflux-authorizationmanager
run_tests "webflux-authorizationmanager" \
    "${SCRIPT_DIR}/webflux-authorizationmanager/src/main/resources/policies" \
    "${SCRIPT_DIR}/webflux-authorizationmanager/src/test/resources/unit" \
    "${SCRIPT_DIR}/webflux-authorizationmanager/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --condition-hit-ratio 100

# web-authorizationmanager
run_tests "web-authorizationmanager" \
    "${SCRIPT_DIR}/web-authorizationmanager/src/main/resources/policies" \
    "${SCRIPT_DIR}/web-authorizationmanager/src/test/resources/unit" \
    "${SCRIPT_DIR}/web-authorizationmanager/target/sapl-testcommand-coverage" \
    --policy-set-hit-ratio 100 --condition-hit-ratio 100

# mqtt
run_tests "mqtt" \
    "${SCRIPT_DIR}/mqtt/src/main/resources/policies" \
    "${SCRIPT_DIR}/mqtt/src/test/resources/unit" \
    "${SCRIPT_DIR}/mqtt/target/sapl-testcommand-coverage" \
    --policy-hit-ratio 100 --condition-hit-ratio 100

echo "========================================"
echo "Results: $((total - failures))/${total} passed"
if [ "${failures}" -ne 0 ]; then
    echo "${failures} module(s) failed."
    exit 1
fi
echo "All modules passed."
