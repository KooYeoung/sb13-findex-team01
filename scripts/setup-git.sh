#!/bin/sh

git config --local commit.template .gitmessage.txt
git config --local core.hooksPath .githooks

chmod +x .githooks/commit-msg

echo "Git commit template and hooks have been configured."
echo "Commit message example: Add: 지수 정보 생성 API 추가"