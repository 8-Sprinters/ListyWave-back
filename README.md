# ListyWave: 내가 큐레이팅한 리스트로 소통하는 SNS 서비스

<br>

<div align="center">
  <div>
    <img alt="logo" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/0d0f39e5-9070-43e2-8652-dd9b87dc5843">
  </div>
  <br>
  <h3>리스티웨이브에서 모든 기준은 내 ‘취향'입니다.</h3>
  <p>내 취향을 기록하고, 다른 사람에게 공유하고, 그 과정 속에서 나를 발견해요.</p>
</div>

<br>

## 목차

1. [서비스 스크린샷](#서비스-스크린샷-)
2. [기술 스택](#기술-스택)
3. [Infra Architecture 및 CI/CD](#infra-architecture-및-cicd)
4. [이미지 처리 흐름도](#이미지-처리-흐름도)
5. [ERD](#erd)
6. [멤버](#멤버)

<br>

## 서비스 스크린샷 📸

<div align="center">
  <h2>✨ 인트로</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/60532a0f-97c3-4954-818c-76502ec88164">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/e6984e11-9d1d-4a59-8e7a-3cb7f7ccfddf">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 온보딩</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/4d80227f-aa75-4a16-93c9-fa166146421b">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/cc3e94eb-77b2-4ba8-a212-b8fd5037568b">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 리스트 탐색</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/b0506308-fe9a-4a9f-a556-9fae636bb73b">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/214d97ef-e26c-427f-93b2-abf8c63b61b3">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 리스트 검색</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/e955bb8e-8bf4-4d73-9c37-b881103c2d2a">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/d295d58c-a37f-4cd1-8260-1ae4fb91fa0f">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 리스트 생성</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/94a34e78-c67d-44c8-9f24-d2bac0a00885">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/efc4d864-1717-48c0-bc40-28eb7ebdd90a">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 리스트 수정</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/a5289211-78c5-4601-924a-e0738e99e60d">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/5172f288-30e5-4c6d-8d2b-e411995470c4">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 리스트 상세 조회</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/3f0e58b0-7c90-4231-94f9-ce8da792a020">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/02781e2f-406c-4c02-894b-535ee41de83c">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 히스토리 조회</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/4fdabc6f-55a3-4340-b2b6-4775e4fbfb38">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/ec52fac3-4659-4520-ba73-e16803bd1c9d">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 마이리스트/콜라보리스트 조회</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/21da372a-3ca8-4e59-b4e2-8c5460345224">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/25cf11ca-6f75-401f-bad5-06908780ce83">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 콜렉션 조회</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/81800c4a-2ea9-46de-8a06-b4674ba87ed8">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/dae42642-9870-4ee9-b5bd-af4f8bbfdec0">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 마이 페이지 - 언어 변경, 로그아웃, 회원 탈퇴</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/8a245adc-9c1b-4d6a-9930-e5ad12e9313b">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/b7722990-6f77-434b-ba10-eb881436b067">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/4ea80eec-c37f-417b-ad58-9151fecbbe5b">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 프로필 수정</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/7538026d-d7e4-4e17-a594-93b70bd528f8">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/685775f9-0fef-4a73-871e-0afa9870df4c">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 팔로우/팔로잉</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/b882facd-5fc4-48e2-ba77-7e21fd6dd182">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/fed3026f-6111-49bb-acaa-d326193881eb">
  </div>
</div>

<br/>

<div align="center">
  <h2>✨ 알림</h2>
  <div>
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/124856726/0a651468-fb80-4fb0-b6f8-c0e3dfd820e0">
    <img width="275" height="620" alt="온보딩 페이지" src="https://github.com/8-Sprinters/ListyWave-front/assets/96380950/08184267-a444-49d1-86cf-26214773a3ab">
  </div>
</div>

## 기술 스택

![기술 스택](https://github.com/8-Sprinters/ListyWave-back/assets/66300965/ede73d1d-e3f7-4a21-9add-590dca23436a)

## Infra Architecture 및 CI/CD

![인프라 아키텍처 및 CI_CD](https://github.com/8-Sprinters/ListyWave-back/assets/66300965/1e5450ac-401c-43c6-b3a9-9caa3e36e184)

## 이미지 처리 흐름도

![이미지 요청 흐름도](https://github.com/8-Sprinters/ListyWave-back/assets/66300965/749ce6b8-55d0-45ff-b4ba-c4264bff8589)

## ERD

![image](https://github.com/8-Sprinters/ListyWave-back/assets/107859870/ea329fc2-ec0e-4ae6-b1a5-2903ac3f3bca)

## 멤버

### 백엔드

| <img src="https://avatars.githubusercontent.com/u/107859870?s=400&u=5a1dffc8cb837b44ec26ae7468499972953fc913&v=4" width="130" height="130"> | <img src ="https://avatars.githubusercontent.com/u/66300965?v=4" width="130" height="130"> |
|:-------------------------------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------:|
|                                                      [박정수](https://github.com/pparkjs)                                                      |                             [김동호](https://github.com/kdkdhoho)                             |

### 프론트엔드

| <img src="https://avatars.githubusercontent.com/u/70089733?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/142777396?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/124856726?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/144652458?v=4" width="130" height="130"> | <img src="https://avatars.githubusercontent.com/u/96380950?v=4" width="130" height="130"> |
|:-----------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|
|                           [안유진](https://github.com/Eugene-A-01)                           |                           [강나현](https://github.com/Nahyun-Kang)                            |                           [박소현](https://github.com/ParkSohyunee)                           |                           [민서영](https://github.com/seoyoung-min)                           |                           [강현지](https://github.com/kanglocal/)                            |

### 디자이너

| <img src="https://avatars.githubusercontent.com/u/122449772?v=4" width="130" height="130"> |
|:------------------------------------------------------------------------------------------:|
|                             [신은서](https://github.com/Ain1204)                              |
