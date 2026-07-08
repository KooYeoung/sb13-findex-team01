# Git Commit Template & Commit Convention 적용 가이드

## 1. 목적

본 문서는 프로젝트에서 커밋 메시지 컨벤션을 일관되게 유지하기 위한 설정 방법을 정리한 문서입니다.

팀원마다 다른 형식으로 커밋 메시지를 작성하면 변경 이력을 파악하기 어렵기 때문에, 아래와 같은 방식을 사용합니다.

* `git commit` 실행 시 커밋 메시지 템플릿 자동 표시
* 커밋 메시지 형식이 맞지 않으면 커밋 차단
* 팀원이 동일한 설정을 쉽게 적용할 수 있도록 설정 스크립트 제공

---

## 2. 적용할 커밋 컨벤션

커밋 메시지는 아래 형식을 사용합니다.

```text
타입: 커밋 내용
```

예시:

```text
Add: 지수 정보 생성 API 추가
Fix: 지수 데이터 중복 저장 오류 수정
Refactor: 외부 API 호출 로직 분리
Docs: README 실행 방법 추가
Rename: SyncHistory를 SyncJob으로 변경
```

---

## 3. 커밋 타입 규칙

| 타입       | 설명               |
| -------- | ---------------- |
| Add      | 새로운 파일 또는 기능 추가  |
| Fix      | 코드 수정 또는 버그 수정   |
| Refactor | 기능 변경 없는 코드 리팩토링 |
| Docs     | README와 같은 문서 수정 |
| Rename   | 파일, 폴더명 수정 또는 이동 |

---

## 4. 프로젝트 디렉터리 구조

프로젝트 루트에 아래 파일들을 추가합니다.

```text
findex/
 ├── .gitmessage.txt
 ├── .githooks/
 │    └── commit-msg
 ├── scripts/
 │    ├── setup-git.sh
 │    └── setup-git.bat
 └── docs/
      └── git-commit-template.md
```

각 파일의 역할은 다음과 같습니다.

| 파일                            | 역할                                            |
| ----------------------------- | --------------------------------------------- |
| `.gitmessage.txt`             | 커밋 메시지 작성 시 기본으로 표시될 템플릿                      |
| `.githooks/commit-msg`        | 커밋 메시지 형식을 검사하는 Git hook                      |
| `scripts/setup-git.sh`        | Mac/Linux/Git Bash 환경에서 Git 설정을 적용하는 스크립트     |
| `scripts/setup-git.bat`       | Windows CMD/PowerShell 환경에서 Git 설정을 적용하는 스크립트 |
| `docs/git-commit-template.md` | 설정 방법과 사용법을 정리한 문서                            |

---

## 5. 커밋 메시지 템플릿 작성

프로젝트 루트에 `.gitmessage.txt` 파일을 생성합니다.

```text
# 50자 이하의 간단한 제목을 사용합니다.
# ex) Add: gildong.java
# ex) Refactor: 완전 탐색 -> 이분 탐색

####################
# Add: 새로운 파일 추가
# Fix: 코드 수정
# Refactor: 코드 리팩토링
# Docs: README와 같은 문서 수정
# Rename: 파일, 폴더명 수정 혹은 이동
####################

# 템플릿 사용 방법
# 1. git add . 혹은 git add <파일명> 입력
# 2. git commit
# 3. 제목 작성
# 4. 파일 저장 후 닫기
# 5. 커밋 완료
```

이 파일을 설정하면 `git commit` 입력 시 위 템플릿이 자동으로 표시됩니다.

---

## 6. commit-msg Hook 작성

커밋 메시지 형식을 강제하기 위해 `.githooks/commit-msg` 파일을 생성합니다.

```bash
mkdir -p .githooks
touch .githooks/commit-msg
chmod +x .githooks/commit-msg
```

`.githooks/commit-msg` 파일에 아래 내용을 작성합니다.

```bash
#!/bin/sh

COMMIT_MSG_FILE="$1"

# 주석과 빈 줄을 제외한 첫 번째 줄을 커밋 제목으로 사용
TITLE=$(grep -vE '^[[:space:]]*#' "$COMMIT_MSG_FILE" | sed '/^[[:space:]]*$/d' | head -n 1)

# 제목이 비어있는지 검사
if [ -z "$TITLE" ]; then
  echo ""
  echo "커밋 메시지를 입력해주세요."
  echo ""
  echo "사용 예시:"
  echo "  Add: 지수 정보 생성 API 추가"
  echo "  Fix: 지수 데이터 중복 저장 오류 수정"
  echo "  Refactor: 동기화 서비스 로직 분리"
  echo "  Docs: README 실행 방법 추가"
  echo "  Rename: IndexInfoDto 파일명 변경"
  echo ""
  exit 1
fi

# 50자 이하 검사
TITLE_LENGTH=$(printf "%s" "$TITLE" | wc -m | tr -d ' ')

if [ "$TITLE_LENGTH" -gt 50 ]; then
  echo ""
  echo "커밋 제목은 50자 이하로 작성해주세요."
  echo "현재 글자 수: $TITLE_LENGTH"
  echo "현재 제목: $TITLE"
  echo ""
  echo "수정 예시:"
  echo "  Add: 지수 정보 생성 API 추가"
  echo "  Fix: 중복 저장 오류 수정"
  echo ""
  exit 1
fi

# 허용 타입 검사
if ! echo "$TITLE" | grep -Eq '^(Add|Fix|Refactor|Docs|Rename): .+'; then
  echo ""
  echo "커밋 메시지 형식이 올바르지 않습니다."
  echo ""
  echo "사용 가능한 형식:"
  echo "  Add: 새로운 파일 추가"
  echo "  Fix: 코드 수정"
  echo "  Refactor: 코드 리팩토링"
  echo "  Docs: README와 같은 문서 수정"
  echo "  Rename: 파일, 폴더명 수정 혹은 이동"
  echo ""
  echo "복사해서 사용할 수 있는 예시:"
  echo "  Add: 지수 정보 생성 API 추가"
  echo "  Add: 지수 데이터 조회 API 추가"
  echo "  Add: 연동 이력 저장 기능 추가"
  echo "  Fix: 지수 데이터 중복 저장 오류 수정"
  echo "  Fix: 자동 연동 설정 수정 오류 해결"
  echo "  Refactor: 외부 API 호출 로직 분리"
  echo "  Refactor: 공통 예외 처리 구조 개선"
  echo "  Docs: README 프로젝트 실행 방법 추가"
  echo "  Rename: SyncHistory를 SyncJob으로 변경"
  echo ""
  echo "현재 입력된 커밋 메시지:"
  echo "  $TITLE"
  echo ""
  exit 1
fi

exit 0
```

---

## 7. Git Hook 경로 설정

Git hook은 기본적으로 `.git/hooks` 디렉터리에서 동작합니다.
하지만 `.git/hooks`는 Git에 커밋되지 않기 때문에 팀원 간 공유가 어렵습니다.

따라서 프로젝트 내부의 `.githooks` 디렉터리를 Git hook 경로로 사용하도록 설정합니다.

```bash
git config --local core.hooksPath .githooks
```

이 설정은 로컬 Git 설정이기 때문에, 팀원마다 한 번씩 실행해야 합니다.

---

## 8. Commit Template 설정

커밋 메시지 템플릿을 적용하기 위해 아래 명령어를 실행합니다.

```bash
git config --local commit.template .gitmessage.txt
```

이후 `git commit` 명령어를 실행하면 `.gitmessage.txt` 내용이 기본 커밋 메시지 템플릿으로 표시됩니다.

---

## 9. 팀원용 설정 스크립트 작성

팀원들이 매번 명령어를 직접 입력하지 않아도 되도록 운영체제별 설정 스크립트를 제공합니다.

* Mac/Linux/Git Bash: `scripts/setup-git.sh`
* Windows CMD/PowerShell: `scripts/setup-git.bat`

---

### 9-1. Mac/Linux/Git Bash용 설정 스크립트

`scripts/setup-git.sh` 파일을 생성합니다.

```bash
mkdir -p scripts
touch scripts/setup-git.sh
chmod +x scripts/setup-git.sh
```

`scripts/setup-git.sh` 파일에 아래 내용을 작성합니다.

```bash
#!/bin/sh

git config --local commit.template .gitmessage.txt
git config --local core.hooksPath .githooks

chmod +x .githooks/commit-msg

echo "Git commit template and hooks have been configured."
echo "Commit message example: Add: 지수 정보 생성 API 추가"
```

Mac/Linux 또는 Git Bash 사용자는 프로젝트를 clone 받은 후 아래 명령어를 한 번만 실행하면 됩니다.

```bash
./scripts/setup-git.sh
```

실행 권한 문제가 발생하면 아래처럼 실행합니다.

```bash
sh scripts/setup-git.sh
```

---

### 9-2. Windows CMD/PowerShell용 설정 스크립트

Windows 환경에서는 `.sh` 파일이 기본 CMD 또는 PowerShell에서 바로 실행되지 않을 수 있습니다.
따라서 Windows 사용자를 위해 `scripts/setup-git.bat` 파일을 추가합니다.

`scripts/setup-git.bat` 파일에 아래 내용을 작성합니다.

```bat
@echo off

git config --local commit.template .gitmessage.txt
git config --local core.hooksPath .githooks

echo Git commit template and hooks have been configured.
echo Commit message example: Add: 지수 정보 생성 API 추가

pause
```

Windows 사용자는 프로젝트를 clone 받은 후 PowerShell 또는 CMD에서 아래 명령어를 실행합니다.

```powershell
.\scripts\setup-git.bat
```

또는 파일 탐색기에서 `scripts/setup-git.bat` 파일을 더블 클릭해도 됩니다.

---

## 10. 적용 순서

최초 설정자는 아래 순서로 진행합니다.

```bash
# 1. 커밋 템플릿 파일 생성
touch .gitmessage.txt

# 2. Git hook 디렉터리 생성
mkdir -p .githooks

# 3. commit-msg hook 생성
touch .githooks/commit-msg
chmod +x .githooks/commit-msg

# 4. Mac/Linux/Git Bash용 설정 스크립트 생성
mkdir -p scripts
touch scripts/setup-git.sh
chmod +x scripts/setup-git.sh

# 5. 로컬 Git 설정 적용
git config --local commit.template .gitmessage.txt
git config --local core.hooksPath .githooks
```

Windows 사용자를 위해 `scripts/setup-git.bat` 파일도 함께 생성합니다.

```bat
@echo off

git config --local commit.template .gitmessage.txt
git config --local core.hooksPath .githooks

echo Git commit template and hooks have been configured.
echo Commit message example: Add: 지수 정보 생성 API 추가

pause
```

이후 관련 파일들을 커밋합니다.

```bash
git add .gitmessage.txt .githooks/commit-msg scripts/setup-git.sh scripts/setup-git.bat
git commit
```

커밋 메시지 예시:

```text
Add: Git 커밋 컨벤션 설정 추가
```

---

## 11. 팀원이 프로젝트를 받은 후 해야 할 일

팀원은 프로젝트를 clone 받은 후 본인 운영체제에 맞는 설정 스크립트를 한 번 실행합니다.

### Mac/Linux/Git Bash

```bash
./scripts/setup-git.sh
```

실행 권한 문제로 실행되지 않으면 아래 명령어를 사용합니다.

```bash
sh scripts/setup-git.sh
```

### Windows CMD/PowerShell

```powershell
.\scripts\setup-git.bat
```

또는 아래 명령어를 직접 실행해도 됩니다.

```powershell
git config --local commit.template .gitmessage.txt
git config --local core.hooksPath .githooks
```

정상 적용 여부는 아래 명령어로 확인할 수 있습니다.

```bash
git config --local commit.template
git config --local core.hooksPath
```

정상이라면 아래와 같이 출력됩니다.

```text
.gitmessage.txt
.githooks
```

---

## 12. 커밋 사용 방법

일반적인 커밋 과정은 아래와 같습니다.

```bash
git add .
git commit
```

`git commit`을 입력하면 커밋 메시지 템플릿이 열립니다.

예를 들어 아래와 같이 작성합니다.

```text
Add: 지수 정보 생성 API 추가
```

저장 후 닫으면 커밋이 완료됩니다.

---

## 13. IntelliJ에서 사용하는 방법

IntelliJ에서 커밋할 때도 동일하게 hook이 적용됩니다.

커밋 메시지 형식이 잘못되면 아래와 같은 오류가 발생할 수 있습니다.

```text
커밋 메시지 형식이 올바르지 않습니다.

사용 가능한 형식:
  Add: 새로운 파일 추가
  Fix: 코드 수정
  Refactor: 코드 리팩토링
  Docs: README와 같은 문서 수정
  Rename: 파일, 폴더명 수정 혹은 이동

복사해서 사용할 수 있는 예시:
  Add: 지수 정보 생성 API 추가
  Fix: 지수 데이터 중복 저장 오류 수정
  Refactor: 외부 API 호출 로직 분리
  Docs: README 프로젝트 실행 방법 추가
```

이 경우 IntelliJ 커밋 메시지 입력창에 아래 형식으로 다시 작성하면 됩니다.

```text
Add: 지수 정보 생성 API 추가
```

IntelliJ 콘솔에 보이는 아래 문구는 Git 실행 옵션이 출력된 것이므로 오류 원인은 아닙니다.

```text
--cleanup=strip --
```

실제 오류 원인은 커밋 메시지가 정해진 형식을 따르지 않았기 때문입니다.

---

### Windows에서 IntelliJ를 사용하는 경우

Windows 환경에서 IntelliJ를 사용하는 경우에도 `commit-msg` hook은 동일하게 동작합니다.
다만 처음 프로젝트를 clone 받은 후 아래 설정이 되어 있어야 합니다.

```powershell
.\scripts\setup-git.bat
```

또는 직접 아래 명령어를 실행합니다.

```powershell
git config --local commit.template .gitmessage.txt
git config --local core.hooksPath .githooks
```

Windows에서 `.sh` 파일을 PowerShell이나 CMD에서 실행하면 동작하지 않을 수 있습니다.

```powershell
./scripts/setup-git.sh
```

위 명령어가 실행되지 않는 경우 아래 중 하나를 사용합니다.

```powershell
.\scripts\setup-git.bat
```

또는 Git Bash에서 실행합니다.

```bash
sh scripts/setup-git.sh
```

---

## 14. 올바른 커밋 메시지 예시

```text
Add: 지수 정보 생성 API 추가
Add: 지수 데이터 목록 조회 API 추가
Add: 연동 이력 저장 기능 추가
Fix: 지수 데이터 중복 저장 오류 수정
Fix: 자동 연동 설정 수정 오류 해결
Refactor: 외부 API 호출 로직 분리
Refactor: 공통 예외 처리 구조 개선
Docs: README 프로젝트 실행 방법 추가
Rename: SyncHistory를 SyncJob으로 변경
```

---

## 15. 잘못된 커밋 메시지 예시

```text
수정
작업함
update
fix
Add 지수 정보 생성
add: 지수 정보 생성 API 추가
Feat: 지수 정보 생성 API 추가
Fix: 
```

위 예시가 잘못된 이유는 다음과 같습니다.

| 잘못된 예시                  | 이유             |
| ----------------------- | -------------- |
| `수정`                    | 타입이 없음         |
| `작업함`                   | 변경 내용을 알기 어려움  |
| `update`                | 타입 규칙을 따르지 않음  |
| `Add 지수 정보 생성`          | 콜론이 없음         |
| `add: 지수 정보 생성 API 추가`  | 타입 대소문자가 맞지 않음 |
| `Feat: 지수 정보 생성 API 추가` | 허용된 타입이 아님     |
| `Fix:`                  | 커밋 내용이 없음      |

---

## 16. 설정 해제 방법

커밋 템플릿 설정을 해제하려면 아래 명령어를 사용합니다.

```bash
git config --local --unset commit.template
```

Git hook 경로 설정을 해제하려면 아래 명령어를 사용합니다.

```bash
git config --local --unset core.hooksPath
```

---

## 17. 주의사항

* `.gitmessage.txt`, `.githooks/commit-msg`, `scripts/setup-git.sh`, `scripts/setup-git.bat` 파일은 Git에 커밋해야 팀원들이 받을 수 있습니다.
* `git config --local` 설정은 팀원 개인 로컬 저장소에만 적용됩니다.
* 따라서 프로젝트를 clone 받은 팀원은 반드시 본인 환경에 맞는 설정 스크립트를 한 번 실행해야 합니다.
* Mac/Linux/Git Bash 사용자는 `./scripts/setup-git.sh` 또는 `sh scripts/setup-git.sh`를 실행합니다.
* Windows CMD/PowerShell 사용자는 `.\scripts\setup-git.bat`을 실행합니다.
* Windows에서 `./scripts/setup-git.sh`가 실행되지 않는 것은 정상적인 상황일 수 있습니다.
* Git Bash를 사용하는 경우 Windows에서도 `sh scripts/setup-git.sh` 명령어를 사용할 수 있습니다.
* `.sh` 파일 실행 시 `bad interpreter: /bin/sh^M` 오류가 발생하면 줄바꿈 방식이 CRLF로 되어 있을 가능성이 있습니다.
* `.sh` 파일은 LF 줄바꿈을 사용하는 것이 좋습니다.
* IntelliJ에서는 파일 우측 하단의 줄바꿈 설정을 `CRLF`에서 `LF`로 변경할 수 있습니다.
* `commit-msg` hook은 로컬에서 커밋 메시지를 검사합니다.
* GitHub PR 제목까지 강제하려면 별도의 GitHub Actions 설정이 필요합니다.
* 급하게 커밋해야 할 경우 `--no-verify` 옵션으로 hook을 우회할 수 있지만, 팀 규칙상 사용하지 않는 것을 권장합니다.

```bash
git commit --no-verify
```

---

## 18. 최종 정리

이번 프로젝트에서는 아래 4개 파일을 통해 커밋 컨벤션을 적용합니다.

```text
.gitmessage.txt
.githooks/commit-msg
scripts/setup-git.sh
scripts/setup-git.bat
```

역할은 다음과 같습니다.

```text
.gitmessage.txt
→ git commit 시 커밋 메시지 템플릿 표시

.githooks/commit-msg
→ 커밋 메시지 형식 검사 및 오류 시 커밋 차단

scripts/setup-git.sh
→ Mac/Linux/Git Bash 환경에서 Git 설정 자동화

scripts/setup-git.bat
→ Windows CMD/PowerShell 환경에서 Git 설정 자동화
```

팀원은 프로젝트를 clone 받은 후 본인 환경에 맞는 스크립트를 한 번 실행합니다.

```text
Mac/Linux/Git Bash
→ ./scripts/setup-git.sh

Windows CMD/PowerShell
→ .\scripts\setup-git.bat
```

커밋 메시지는 아래 형식을 따릅니다.

```text
Add: 새로운 파일 또는 기능 추가
Fix: 코드 수정 또는 버그 수정
Refactor: 코드 리팩토링
Docs: 문서 수정
Rename: 파일 또는 폴더명 수정/이동
```
