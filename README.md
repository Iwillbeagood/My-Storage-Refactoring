
<h1 align="center">My Storage Refactoring Version</h1>

<p align="center">
  <img src="https://github.com/Iwillbeagood/My-Storage/assets/106158445/a4559d4b-063f-47c0-8d62-70f6494b9d0a" alt="My Image">
</p>
<br>

>  My Storage는 사용자가 자신이 구매한 생필품이나 물건을 위치별로 관리할 수 있게 하는 애플리케이션입니다. 사용자는 위치로 구분지어진 물건들을 확인할 수 있으며, 물건의 상태를 관리할 수 있습니다.
>  사용자가 물건을 사용 완료 했을시 물건을 '사용 완료'로 옮겨 차후에 다시 구매해야할 물건을 확인할 수 있습니다.
<br>

## Download
최신 APK는 [Releases](https://github.com/Iwillbeagood/My-Storage/releases)에서 다운로드 할 수 있습니다.

<h2>🗊 목차</h2>
<ul>
  <li><a href="#1-프로젝트-배경">1. 프로젝트 배경</a></li>
  <li><a href="#2-사용-기술">2. 사용 기술</a></li>
  <li><a href="#3-Architecture">3. Architecture</a></li>
  <li><a href="#4-Overview">4. Overview</a></li>
  <li><a href="#5-고찰">5. 고찰</a></li>
</ul>
<br>

<h2 id="1-프로젝트-배경">1. 프로젝트 배경</h2>
기존의 프로젝트의 많은 문제가 있었기에 문제를 해결하고 개선하고자 프로젝트를 시작하게 되었습니다.
<br>
<br>

**기존의 프로젝트의 문제점**

1. MVVM을 사용하지만 ViewModel은 View의 인스턴스를 직접 접근해 View를 제어하고 있어  **[Android App Archutecture](https://everyday-develop-myself.tistory.com/208)를** 제대로 지키지 못함
2. ViewModel에서 직접 Retrofit2를 사용하여 네트워크 요청을 처리하는 것은 코드의 복잡성을 증가시킬 수 있으며, 재사용성과 의존성 관리에 어려움을 겪을 수 있다.
3. AWS EC2를 통해 서버를 할당 받았음으로 유지 보수가 힘들고 지속적으로 이용료가 발생
4. 서버에 대한 지식의 부족으로 많은 사용자 정보 보안 문제 발생 
5. 분산화된 프레임워크로 인한 유지 보수가 힘들다. 
6. RecyclerView.adapter의 사용으로 인한 복잡성 증대
<br>

**해결책**
1. 기존의 MVVM을 사용한 패턴에서 Repository를 추가해 네트워크 요청을 따로 처리하고 Hilt를 사용해 의존성을 처리하는 방식으로 기존의 프로젝트를 개선. eventFlow와 LiveData를 사용해 Observer를 적절히 구현해 AAA 원칙을 지킴. 
2. 서버가 필요하지 않게 애플리케이션의 로그인 기능 제거. 
3. Android Jetpack Room을 사용해 데이터베이스 구축해 단일 프레임워크 (Android Studio) 사용
4. RecyclerView.adapter대신 ListAdapter를 사용

   
<h2 id="2-사용-기술">2. 사용 기술</h2>
<div align=center><h1>📚 STACKS</h1></div>
<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> 
  <img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"> 
  <br>
  
  <img src="https://img.shields.io/badge/android studio-3DDC84?style=for-the-badge&logo=android studio&logoColor=white">
  <br>
  
  <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> 
  <br>
  
  <img src="https://img.shields.io/badge/apache-D22128?style=for-the-badge&logo=apache&logoColor=white">   
  <img src="https://img.shields.io/badge/php-777BB4?style=for-the-badge&logo=php&logoColor=white">
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <br>
  
  <img src="https://img.shields.io/badge/visual studio code-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white">
</div>
<br>

<div align=center><h1>Tech stack & Open-source libraries</h1></div>

- Minimum SDK level 26
- [Kotlin](https://kotlinlang.org/)  기반
- Jetpack
  - Lifecycle: 안드로이드 라이프사이클을 관찰하고 라이프사이클 변경에 따라 UI 상태를 처리합니다.
  - ViewModel: UI 관련 데이터 홀더를 관리하며 라이프사이클을 인식합니다. 화면 회전과 같은 구성 변경에서 데이터를 보존할 수 있습니다. 
  - DataBinding: UI 구성요소를 프로그래밍적으로가 아닌 선언적 형식으로 레이아웃에 데이터 소스와 바인딩합니다.
- Architecture
  - MVVM Architecture (View - DataBinding - ViewModel - Model)
  - Bindables: UI 레이어에 데이터 변경을 알리기 위한 Android DataBinding 킷입니다.
- ViewPager2: 메인 페이지의 화면 전환에 사용됩니다.
- [Material-Components](https://github.com/material-components/material-components-android): Material Design 컴포넌트들은 메인 페이지의 이동을 정의하는 TabLayout과 아이템 추가를 위한 FloatingActionButton 그리고 사용자에게 정보를 받을 때 사용되는 Chip, ChipGroup이 사용됩니다.
- [Glide](https://github.com/bumptech/glide), [GlidePalette](https://github.com/florent37/GlidePalette): 네트워크에서 이미지를 불러옵니다.
- Drawerlayout: Menu를 보여주기 위해 사용됩니다.
- Dagger Hilt: 의존성 주입을 위해 Dagger Hilt를 사용합니다. Hilt는 앱의 컴포넌트간 의존성을 관리하고 의존성을 주입하는 기능을 제공합니다.
- Room 라이브러리: Room은 SQLite를 추상화하고 데이터베이스 액세스를 용이하게 처리합니다.
- Gson: JSON 직렬화와 역직렬화를 위해 Gson 라이브러리를 사용합니다.
<br>

<h2 id="3-Architecture">3. Architecture</h2>

 **My Storage**  는 [Google's official architecture guidance](https://developer.android.com/topic/architecture).
의 MVVM architecture Pattern을 따르고 있습니다.
<br>

<p align="center">
  <img src="https://github.com/Iwillbeagood/My-Storage/assets/106158445/bb1ef6e1-50b6-4e55-89cc-b5b99a1ee57e" alt="My Image">
</p>
<br>

<h2 id="4-Overview">4. Overview</h2>
<div align=center><h1>Mobile</h1></div>


### 4-1. 사용자의 집 구조 정보 입력 프로세스

<p align="center">
  <img src="https://github.com/Iwillbeagood/My-Storage/assets/106158445/44e80d7e-c91d-4e7a-a8a7-7b05b327fc60" alt="My Image">
</p>

My Storage는 사용자에게 집 구조에 대한 정보를 입력받습니다. 로그인 후, 데이터베이스에서 사용자의 ID를 검색하고, 집 구조 정보가 없는 경우 이 프로세스가 시작됩니다. 사용자는 다양한 기본 집 구조 옵션 중에서 선택하여 정보를 입력할 수 있습니다. 또한, 선택한 집 구조의 각 방과 화장실에 대한 이름도 변경할 수 있으며, 필요에 따라 구조를 추가할 수도 있습니다.

이를 통해 My Storage는 사용자들이 자신의 집 구조에 대한 정보를 정확하고 자유롭게 입력하고 수정할 수 있도록 지원합니다. 사용자들은 집 구조에 대한 세부 정보를 개인화할 수 있습니다.

<br>
<br>


### 4-2. 물건 표시 프로세스

<p align="center">
  <img src="https://github.com/Iwillbeagood/My-Storage/assets/106158445/a4559d4b-063f-47c0-8d62-70f6494b9d0a" alt="My Image">
</p>

MyStorage의 메인 화면은 '구조', '목록', '사용 완료'라는 세 가지로 물건의 상태에 따라 구분하여 보여줍니다. 각 화면은 Viewpager로 정의되어 있으며, 사용자는 Slide와 바텀 탭을 이용하여 화면 간에 이동할 수 있습니다.

구조 페이지:
구조 페이지에서는 사용자의 물건을 구조에 따라 구분하여 표시합니다. 사용자는 선택한 구조에 어떤 물건이 있는지 한 눈에 확인할 수 있습니다. 상단에 위치한 Spinner를 통해 보여질 구조를 선택할 수 있습니다. 이를 통해 사용자는 특정 구조에 속한 물건을 쉽게 찾을 수 있습니다.

목록 페이지:
목록 페이지에서는 사용자의 물건을 목록 형태로 표시합니다. 사용자는 물건에 대한 자세한 정보를 확인할 수 있습니다. 상단의 검색창을 이용하여 물건을 검색하여 원하는 물건을 빠르게 찾을 수도 있습니다. 이 페이지는 사용자가 보유한 모든 물건을 한 곳에서 확인하고 관리할 수 있는 기능을 제공합니다.

사용 완료 페이지:
사용 완료 페이지에서는 사용자가 사용을 완료한 물건들을 목록 형태로 보여줍니다. 사용자는 사용 완료된 물건에 대한 자세한 정보를 확인할 수 있습니다. 이를 통해 사용자는 다시 구매해야 할 물건들을 파악할 수 있습니다. 이 페이지는 사용자의 구매 이력을 추적하고 필요한 물건들을 관리하는 데 도움을 줍니다.

위와 같은 구성을 가진 MyStorage는 사용자에게 편리하고 직관적인 인터페이스를 제공하여 물건의 상태와 관련된 정보를 손쉽게 확인하고 관리할 수 있도록 도와줍니다.

<br>

<p align="center">
  <img src="https://github.com/Iwillbeagood/My-Storage/assets/106158445/08120206-c94a-4b58-b809-09a136212d4e" alt="My Image">
</p>

<br>
<br>


MyStorage에서는 사용자가 목록 페이지와 사용 완료 페이지에서 물건을 클릭하여 상태를 변경할 수 있습니다. 목록 페이지의 클릭 이벤트는 일반 클릭과 롱 클릭으로 구분됩니다.

1. 일반 클릭:
일반 클릭 시, 선택한 물건의 상태를 변경할 수 있는 레이아웃이 나타납니다. 사용자는 해당 물건에 대해 다음과 같은 상태 변경 옵션을 선택할 수 있습니다: '사용 완료', '하나 사용', '정보 수정', '물건 삭제'. 사용자는 상태 변경 옵션을 선택하여 물건의 상태를 원하는 대로 변경할 수 있습니다.

2. 롱 클릭:
롱 클릭 시, 해당 물건이 선택된 상태로 표시되며 진동이 울립니다. 이를 롱 클릭 모드라고 정의합니다. 롱 클릭 모드에서는 일반 클릭 시 물건의 체크 상태가 변경됩니다. 또한, 롱 클릭 모드에서는 선택한 물건들의 구조 변경과 삭제가 가능한 레이아웃이 나타납니다. 사용자는 선택한 물건들을 다른 구조로 이동하거나 삭제할 수 있습니다. 롱 클릭 모드는 X 버튼을 클릭하여 해제할 수 있습니다.

사용 완료 페이지에서도 물건을 클릭하면 상태 변경 가능한 레이아웃이 나타납니다. 가능한 상태 변경 옵션은 '되돌리기'와 '물건 삭제' 두 가지입니다. 사용자는 해당 물건의 상태를 '되돌리기'로 변경하거나 물건을 삭제할 수 있습니다.

위와 같은 인터페이스를 통해 MyStorage는 사용자가 물건의 상태를 쉽게 변경하고 관리할 수 있도록 지원합니다. 일반 클릭과 롱 클릭을 활용하여 다양한 작업을 편리하게 수행할 수 있습니다.

<br> 
<br>

### 4-3. 메인 화면 프로세스


메인 화면에서는 물건 추가 버튼과 메뉴 버튼이 제공됩니다. 물건 추가 기능은 영수증 정보를 자동으로 추가하는 방법과 수동으로 입력하는 방법 두 가지가 있습니다. 영수증 정보를 자동으로 처리하는 부분은 머신러닝을 통해 구현 중인 단계입니다.

<p align="center">
  <img src="https://github.com/Iwillbeagood/My-Storage/assets/106158445/6b71b2f1-a8d0-48bc-be41-6eb5a95765c3" alt="My Image">
</p>

<br>

물건 추가:
물건 추가 버튼을 클릭하면 사용자는 물건을 추가할 수 있는 옵션을 선택할 수 있습니다. 사용자는 물건의 정보를 수동으로 입력하여 목록에 추가할 수 있습니다.


 <div align=center> 
   <a href="https://everyday-develop-myself.tistory.com/135">카메라 갤러리 저장소 접근권한 부여에 대한 블로그 링크</a>
  </div>

<br>

<p align="center">
  <img src="https://github.com/Iwillbeagood/My-Storage/assets/106158445/d90429d3-7669-41d3-9573-e1fee46a44c1" alt="My Image">
</p>
<br>

메뉴:
메뉴 버튼을 클릭하면 다양한 메뉴 옵션을 볼 수 있습니다. 이 중에서는 다음과 같은 탭이 제공됩니다:

'구조 재설정하기': 사용자는 구조를 재설정할 수 있는 옵션을 선택할 수 있습니다. 이를 통해 사용자는 구조에 변화가 있을 때 필요한 조정을 할 수 있습니다.

'구조 이름 변경하기': 사용자는 구조의 이름을 변경할 수 있는 옵션을 선택할 수 있습니다. 이를 통해 사용자는 구조의 이름을 원하는 대로 수정할 수 있습니다.

'창고에 물건 전부 제거하기': 사용자는 창고에 있는 모든 물건을 제거할 수 있는 옵션을 선택할 수 있습니다. 이를 통해 사용자는 창고를 초기화하거나 물건을 일괄적으로 삭제할 수 있습니다.


위와 같은 기능과 옵션을 제공하는 메인 화면은 사용자가 물건을 효율적으로 추가하고 관리할 수 있도록 도와줍니다. 

<div align=center><h1>Backend</h1></div>
<h4 align="center">My Storage의 Backend 코드는 아래의 Github Repository에서 확인할 수 있습니다.</h1>
<br>
 <div align=center> 
   <a href="https://github.com/Iwillbeagood/My-Storage-PHP">My Storage Backend 링크</a>
  </div>
  
  <br>
  
   <div align=center> 
   <a href="https://everyday-develop-myself.tistory.com/category/Backend">서버에 대한 블로그 링크</a>
  </div>

  
<br>



