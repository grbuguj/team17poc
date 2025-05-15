진행사항

바코드 인식
유통기한 인식 (정확도 높이기 위한 추가 작업 필요함)

구글, 카카오 로그인 구현



-- Github 사용법 통일 제시 -- 

# 📌 GitHub 브랜치 워크플로우 템플릿 (Issue 기반 개발)

---

## ✅ 1. `main` 브랜치 최신화
```
git checkout main
git pull origin main
```

---

## ✅ 2. GitHub에서 Issue 생성
- 예: `#5 사용자 로그인 기능 추가`
- 체크리스트 예시:
  - [ ] 로그인 페이지 UI 구성
  - [ ] DB 회원 정보 조회
  - [ ] 세션 저장

---

## ✅ 3. 작업 브랜치 생성
```
git checkout -b feat/#5-login
```
- 브랜치 명명 규칙:
  - `feat/`: 기능 추가
  - `fix/`: 버그 수정
  - `refactor/`: 리팩토링
  - `docs/`: 문서 작업

---

## ✅ 4. 개발 & 커밋 & 푸시
```
git add .
git commit -m "feat: #5 로그인 페이지 생성 및 세션 처리"
git push origin feat/#5-login
```
- 커밋 메시지에 `#이슈번호` 포함 (자동 연결됨)

---

## ✅ 5. GitHub에서 Pull Request(PR) 생성
- `base: main` ← `compare: feat/#5-login`
- PR 제목 예: `[#5] 로그인 기능 완료`
- PR 내용 예:
  - 로그인 기능 구현
  - 세션 저장 처리
  - `/login` 경로 추가

---

## ✅ 6. PR Merge
- **Squash and Merge** 방식 추천
- PR에 `Closes #5` 포함하면 Issue 자동 종료

---

## ✅ 7. 브랜치 삭제 (선택)
```
git branch -d feat/#5-login
git push origin --delete feat/#5-login
```

---

## ✅ 8. main 최신화 후 다음 작업 시작
```
git checkout main
git pull origin main
```

---

## 🔁 전체 흐름 요약
`main` → Issue 생성 → 브랜치 생성 → 개발 & 커밋 → PR 생성 → 리뷰 & 머지 → 브랜치 정리 → main 최신화

