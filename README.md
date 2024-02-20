# pposonggil

- Pull
    - git checkout develop : develop으로 branch를 변경합니다.
    - git pull origin develop : git에서 develop코드를 가져옵니다.
      
- 개발
    - project에 item을 작성합니다.
    - item을 In Progress로 가져오고 Issue로 Convert합니다.
        - branch명 : feature-OOO
    - Odsay에 IP를 등록합니다.
    - docker-compose up -d : 서버 실행
    - docker-compose down : 서버 다운
      
- Push & Pull request
    - 변경된 파일들을 git add 합니다.
    - 수정한 날짜 및 변경사항들을 git commit로 작성합니다.
    - git push로 feature-OOO에 push합니다.
    - feature-OOO —> develop으로 pull request를 작성합니다.
        - 변경사항을 작성합니다.
    - 2명 이상의 승인을 받고 merge pull request를 진행합니다.
    - Project에서 item를 삭제합니다.
    - git branch -d feature-OOO로 local branch를 삭제합니다.
    - git branch -D remotes/origin/feature-OOO로 remote branch를 삭제합니다.
      
- Pull request
    - Comment || Approve || Request changes로 review를 합니다.
    - 작성자가 승인받아 merge를 했을 시 develop에서 push된 develop 코드를 pull 합니다.
        - git checkout develop
        - git pull origin develop
    - 개발하고 있던 branch로 돌아와 develop를 merge합니다.
        - git checkout feature-OOO
        - git checkout develop
          
- https://www.youtube.com/watch?v=tkkbYCajCjM 참고
