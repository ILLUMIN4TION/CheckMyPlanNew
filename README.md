# 2025-05-23 commit

## RoomDB + RecyclerView를 사용한 메모 및 일정 기능(CRUD) 구현 완료'

## 미구현 기능
### 태그를 통한 일정 정렬, 일정 선택삭제 등등















#===========================================================================

#2025-05-21 commit

## RoomDB + RecyclerView를 사용한 메모 프래그먼트 + 메모 디테일 액티비티 구현


## Room DB(Memo) 내부 구조, 
### MemoEntity(엔티티 , 테이블정의)
### MemoDao (Dao, 엔티티에 대한 CRUD 정의)
### MemoDatabase (코드에서 우리가 사용할 실제 데이터베이스, 싱글톤으로 구현하여 모든 액티비티(프래그먼트)에서 일관된 Dao 사용
### MyApplication (데이터베이스 업데이트시, 자동으로 버전 업그레이드를 위함)




### memo_rv_item.xml의 TextView 2개( Title, Desc) 에 대한 MaxLines, ellipsize end) 각각 지정



