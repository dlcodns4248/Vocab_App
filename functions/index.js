const {onSchedule} = require("firebase-functions/v2/scheduler");
const {setGlobalOptions} = require("firebase-functions/v2");
const admin = require("firebase-admin");

admin.initializeApp();

// 배포 지역을 서울(asia-northeast3)로 설정 (선택 사항이나 권장)
setGlobalOptions({region: "asia-northeast3"});

exports.sendReviewNotification = onSchedule("every 5 minutes", async (event) => {
  const now = admin.firestore.Timestamp.now();

  const bufferTime = 60 * 1000;
  const nowPlusBuffer = admin.firestore.Timestamp.fromMillis(Date.now() + bufferTime);

  // 1. 복습 대상자 조회
  const snapshot = await admin.firestore().collectionGroup("vocabularies")
    .where("nextReviewDate", "<=", nowPlusBuffer)
    .where("isStudying", "==", true)
    .get();

  if (snapshot.empty) {
    console.log("복습 대상이 없습니다.");
    return;
  }

  for (const doc of snapshot.docs) {
    const vocabData = doc.data();
    // 부모 문서(users/{userId})로 올라가서 fcmToken 확인
    const userDoc = await doc.ref.parent.parent.get();
    const userData = userDoc.data();
    const fcmToken = userData ? userData.fcmToken : null;

    if (fcmToken) {
      const message = {
        notification: {
          title: "복습할 시간이에요! 📖",
          body: `[${vocabData.title || "단어장"}]의 다음 단계 학습이 가능합니다.`,
        },
        token: fcmToken,
      };

      try {
        await admin.messaging().send(message);
        // 중복 방지를 위해 상태 변경
        await doc.ref.update({
                  nextReviewDate: admin.firestore.FieldValue.delete()
                });
        console.log(`알림 발송 성공: ${doc.id}`);
      } catch (error) {
        console.error("FCM 발송 에러:", error);
      }
    }
  }
});