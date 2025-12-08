package com.test.testopia.config.testDataInitialilzer;

import com.test.testopia.test.entity.ChoiceEntity;
import com.test.testopia.test.entity.QuestionEntity;
import com.test.testopia.test.entity.TestEntity;
import com.test.testopia.test.entity.TestResultTypeEntity;
import com.test.testopia.test.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 여행 스타일 테스트 초기 데이터 세팅
 * - 20문항, 4점 리커트, 총점(0~60점)으로 결과 구분
 */
@Component
@RequiredArgsConstructor
public class TravelStyleTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    // 20문항 * 3점 = 60점
    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "여행 스타일 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        // 1. TestEntity 생성
        TestEntity test = TestEntity.builder()
                .id(6L)
                .name(TEST_NAME)
                .description("나에게 잘 맞는 여행 유형, 동행 스타일, 여행지 분위기를 알아보는 여행 성향 테스트입니다.")
                .testNum(6) // /test/6 카드에 맞춰 설정 (겹치면 조정)
                .build();

        // 2. 질문 리스트 정의 (20문항)
        List<String> questions = new ArrayList<>();

        // ▣ 계획 vs 즉흥 / 여행 준비 스타일 (1~7)
        questions.add("여행을 떠나기 전, 일정과 동선을 꽤 꼼꼼하게 짜보는 편이다.");                 // 1
        questions.add("맛집, 카페, 관광지 정보를 미리 찾아보고 리스트업해 두는 것을 좋아한다.");      // 2
        questions.add("여행지에서 길을 헤매더라도, 새로운 곳을 발견하는 재미가 있다고 느낀다.");     // 3
        questions.add("계획에 없던 장소나 활동이 생겨도, 큰 부담 없이 일정에 끼워 넣을 수 있다.");  // 4
        questions.add("여행 중 발생하는 변수(날씨, 교통, 휴무 등)를 나름 유연하게 받아들이는 편이다."); // 5
        questions.add("비행기·기차·숙소 예약 같은 실무적인 준비를 주도하는 편이다.");              // 6
        questions.add("여행 전 설렘과 준비 과정 자체를 하나의 '취미'처럼 즐긴다.");                // 7

        // ▣ 휴양 vs 액티비티 / 취향 요소 (8~13)
        questions.add("현지 카페나 숙소에서 느긋하게 시간을 보내는 것만으로도 충분히 만족스럽다.");      // 8
        questions.add("자연 풍경(바다, 산, 호수 등)을 보며 쉬는 시간에 큰 힐링을 느낀다.");             // 9
        questions.add("박물관, 전시, 역사 유적 등 '배움'이 있는 여행 코스를 좋아하는 편이다.");         // 10
        questions.add("테마파크, 액티비티, 체험 프로그램 등 에너지 쓰는 활동도 즐기는 편이다.");        // 11
        questions.add("현지 음식과 로컬 문화를 직접 경험해 보는 것이 여행의 핵심이라고 생각한다.");      // 12
        questions.add("사진을 많이 찍거나 기록을 남기는 편이라, 인생샷 스팟에 관심이 많다.");           // 13

        // ▣ 동행 스타일 / 혼자 vs 함께 / 분위기 (14~20)
        questions.add("혼자 떠나는 여행도 충분히 즐길 수 있을 것 같다.");                            // 14
        questions.add("친한 몇 명과 소규모로 움직이는 여행이 가장 편하다고 느낀다.");                 // 15
        questions.add("여행 동행자의 컨디션이나 취향을 맞춰주려 노력하는 편이다.");                    // 16
        questions.add("동행과 잠깐 따로 움직이는 시간(각자 하고 싶은 걸 하는 시간)이 있어도 괜찮다."); // 17
        questions.add("여행지에서 새로운 사람을 만나거나 소통하는 것도 나름 즐기는 편이다.");          // 18
        questions.add("여행 중에 생긴 작은 트러블은, 시간이 지나면 웃으면서 추억으로 소비하는 편이다."); // 19
        questions.add("돌아와서 '다음에는 어디를 가볼까?'를 곧잘 상상해 보곤 한다.");                   // 20

        // 3. 질문 + 선택지 생성
        for (int i = 0; i < questions.size(); i++) {
            test.addQuestion(createQuestion(i + 1, questions.get(i)));
        }

        // 4. 결과 유형 생성 후 Test에 연결
        List<TestResultTypeEntity> results = createTestResults(test);
        for (TestResultTypeEntity r : results) {
            test.addResultType(r);
        }

        // 5. 저장
        testRepository.save(test);
        System.out.println(TEST_NAME + " 초기 데이터(20문항, 4유형)가 성공적으로 저장되었습니다.");
    }

    /**
     * 질문 한 개와 4개의 선택지를 생성하는 헬퍼 메서드
     * (모든 문항 정방향 코딩)
     */
    private QuestionEntity createQuestion(int orderNo, String text) {
        QuestionEntity question = QuestionEntity.builder()
                .orderNo(orderNo)
                .text(text)
                .build();

        question.addChoice(ChoiceEntity.builder()
                .orderNo(1).text(TEXT_VERY_MUCH).score(3).build());
        question.addChoice(ChoiceEntity.builder()
                .orderNo(2).text(TEXT_SOMEWHAT).score(2).build());
        question.addChoice(ChoiceEntity.builder()
                .orderNo(3).text(TEXT_NOT_MUCH).score(1).build());
        question.addChoice(ChoiceEntity.builder()
                .orderNo(4).text(TEXT_NOT_AT_ALL).score(0).build());

        return question;
    }

    /**
     * 총점 구간에 따른 결과 유형 4개 정의
     *  - 46~60 : 자유로운 모험가형
     *  - 31~45 : 균형 잡힌 탐험가형
     *  - 16~30 : 감성 힐링형
     *  - 0~15  : 안정/안락형
     */
    private List<TestResultTypeEntity> createTestResults(TestEntity test) {

        TestResultTypeEntity r1 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(46)
                .maxScore(MAX_TOTAL_SCORE)
                .resultName("자유로운 모험가형")
                .description(
                        "●새로운 장소, 새로운 경험을 적극적으로 찾는 타입의 여행자입니다. \n"
                                + "●즉흥적인 코스 변경도 즐기고, 로컬 맛집·액티비티·숨은 명소를 찾아다니는 스타일에 가깝습니다. \n"
                                + "●동남아/유럽 배낭여행, 로드트립, 액티비티 중심 여행과 잘 어울립니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("균형 잡힌 탐험가형")
                .description(
                        "●어느 정도 계획을 세우되, 여유와 변수도 허용하는 균형형 여행자입니다. \n"
                                + "●관광·맛집·휴식·체험을 골고루 섞는 코스를 좋아하며, 동행과의 합을 중요하게 여깁니다. \n"
                                + "●도시 관광 + 근교 자연, 일정 짜인 패키지 + 자유 일정 조합 등이 잘 맞습니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("감성 힐링형")
                .description(
                        "●바쁘게 돌아다니기보다는, 쉼과 분위기를 충분히 느끼는 여행을 선호하는 유형입니다. \n"
                                + "●예쁜 숙소, 감성 카페, 조용한 산책로, 여유로운 일정이 잘 맞으며, 과한 액티비티는 피로할 수 있습니다. \n"
                                + "●호캉스, 섬 여행, 소도시 감성 여행과 궁합이 좋습니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("안정/안락형 여행자")
                .description(
                        "●익숙한 사람, 익숙한 환경, 예측 가능한 일정에서 편안함을 느끼는 유형입니다. \n"
                                + "●장거리·과한 일정보다는, 가까운 곳에서 쉬고 먹고 수다 떠는 여행이 잘 맞을 수 있습니다. \n"
                                + "●집처럼 편한 숙소, 교통이 편한 도시, 짧고 가벼운 일정부터 시작해 보는 것도 좋습니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
