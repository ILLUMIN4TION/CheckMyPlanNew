#2025-05-30 push

## 메모, 일정, 캘린더 기능 구현 및 일정에서 알림을 추가하고 기기에 표시할 수 있게끔 수정


## 알림과 관련된 파일은 notification 패키지에 있습니다
### AlarmReceiver (알림으로 어떤 내용이 들어가야하는지 함수로 정의
### AlarmScheduler (기존의 알람이 존재하는지 PendingIntent를 통해 확인 및 알람 예약 시 현재 시간보다 이전일 경우 방지)
### MemoDatabase (코드에서 우리가 사용할 실제 데이터베이스, 싱글톤으로 구현하여 모든 액티비티(프래그먼트)에서 일관된 Dao 사용
### NotificationHelper (알림과 관련된 다양한 메서드들을 모아둔 파일 ex, status에 따른 분기점으로 스테이터스, 팝업, 전체화면 설정 , 채널 설정)




### memo_rv_item.xml의 TextView 2개( Title, Desc) 에 대한 MaxLines, ellipsize end) 각각 지정



