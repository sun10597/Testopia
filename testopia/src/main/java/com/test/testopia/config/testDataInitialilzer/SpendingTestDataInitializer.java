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
 * 나의 소비 성향 테스트 초기 데이터 세팅
 * - 20문항, 4점 리커트, 총점(0~60점)으로 결과 구분
 * - 점수가 높을수록 '플렉스/경험 소비' 성향이 강한 편
 */
@Component
@RequiredArgsConstructor
public class SpendingTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    // 20문항 * 3점 = 60점
    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "나의 소비 성향 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        // 1. TestEntity 생성
        TestEntity test = TestEntity.builder()
                .id(7L)
                .name(TEST_NAME)
                .description("나는 '플렉스형'일까 '짠돌이형'일까? 나의 지출 습관과 돈에 대한 태도를 알아보는 소비 성향 테스트입니다.")
                .testNum(7) // /test/7 카드에 맞춰 설정 (겹치면 조정)
                .build();

        // 2. 질문 리스트 정의 (20문항)
        List<String> questions = new ArrayList<>();

        // ▣ 전반적인 소비 태도 (1~7)
        questions.add("사고 싶은 것이 생기면, 며칠을 고민하기보다는 비교적 바로 사는 편이다.");                     // 1
        questions.add("돈을 쓸 때 '지금 이 순간의 만족'이 매우 중요하다고 느낀다.");                            // 2
        questions.add("가격이 조금 비싸더라도, 내가 진짜 마음에 든다면 지불할 의향이 있다.");                     // 3
        questions.add("월급이나 용돈을 받으면, 먼저 떠오르는 것은 '저축'보다 '무엇을 해볼까' 하는 생각이다.");        // 4
        questions.add("큰돈이 아니더라도, 자주 소소하게 나를 위한 소비를 하는 편이다.");                         // 5
        questions.add("내가 고생한 날에는, 나 자신에게 보상하는 의미로 소비를 해도 괜찮다고 생각한다.");            // 6
        questions.add("내가 좋아하는 브랜드나 취향에는, 어느 정도 프리미엄을 지불해도 된다고 느낀다.");             // 7

        // ▣ 충동구매 / 감정 소비 (8~13)
        questions.add("기분이 안 좋을 때, 쇼핑이나 배달 음식을 통해 스트레스를 푸는 편이다.");                     // 8
        questions.add("할인·특가·이벤트 문구를 보면, 원래 살 계획이 없던 것도 눈에 들어온다.");                    // 9
        questions.add("SNS나 유튜브에서 본 제품/맛집을, 충동적으로 따라 사거나 가본 적이 자주 있다.");              // 10
        questions.add("'지금 안 사면 나중에 못 살 것 같다'는 생각이 들면, 계획에 없던 지출도 하게 된다.");            // 11
        questions.add("카드 명세서를 보고 '생각보다 많이 썼네'라고 놀라는 경우가 종종 있다.");                      // 12
        questions.add("한 번 꽂힌 취미나 관심사에는, 짧은 기간에 돈을 몰아서 쓰는 경향이 있다.");                    // 13

        // ▣ 저축 / 투자 / 돈에 대한 가치관 (14~20)
        questions.add("정기적인 저축이나 투자보다, 필요할 때 그때그때 모아서 쓰는 편이 더 편하다.");                  // 14
        questions.add("통장 잔고가 줄어드는 것을 보면서도, '그래도 즐겁게 썼으면 된 것 아닌가'라는 생각이 든다.");      // 15
        questions.add("미래의 불확실한 걱정보다, 지금 누릴 수 있는 것에 돈을 쓰는 게 낫다고 느낀다.");                // 16
        questions.add("지출을 기록하거나 가계부를 쓰는 습관을 유지하는 것이 잘 안 된다.");                             // 17
        questions.add("주변 사람들에 비해, 나는 소비에 관대하거나 '플렉스'에 가까운 편이라고 느낀다.");                 // 18
        questions.add("여행, 맛집, 취미 활동 같은 경험에 돈을 쓰는 것은 '가치 있는 지출'이라고 생각한다.");              // 19
        questions.add("모았던 돈을 한 번에 쓰더라도, 만족스럽다면 크게 후회하지 않는 편이다.");                       // 20

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
     * (모든 문항 정방향 코딩: 점수가 높을수록 소비 성향↑)
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
     *  - 46~60 : 플렉스형 경험 소비자
     *  - 31~45 : 균형형 실속 소비자
     *  - 16~30 : 계획형 절약가
     *  - 0~15  : 불안/과절약형
     */
    private List<TestResultTypeEntity> createTestResults(TestEntity test) {

        TestResultTypeEntity r1 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(46)
                .maxScore(MAX_TOTAL_SCORE)
                .resultName("플렉스형 경험 소비자")
                .description(
                        "●현재의 만족과 경험에 과감하게 투자하는 소비 스타일입니다. \n"
                                + "●좋아하는 것에는 아끼지 않고, 여행·취미·맛집·브랜드 등에서 '나를 위한 소비'를 중시합니다. \n"
                                + "●다만, 장기적인 저축/투자 계획을 함께 세운다면 더 안정적인 삶과 즐거움을 동시에 누릴 수 있습니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("균형형 실속 소비자")
                .description(
                        "●쓸 때는 쓰고, 아낄 때는 아끼는 '가성비' 중심 소비 스타일입니다. \n"
                                + "●가격 대비 만족도를 따지는 편이며, 필요와 욕구 사이에서 적당한 균형을 찾으려 합니다. \n"
                                + "●지금처럼 소비 기준을 유지하면서, 자신이 정말 중요하게 생각하는 영역에는 조금 더 투자해 보는 것도 좋습니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("계획형 절약가")
                .description(
                        "●지출에 신중하고, 불필요한 소비를 줄이려는 성향이 강한 편입니다. \n"
                                + "●저축과 안정감을 중요하게 여기며, 충동구매보다는 충분히 고민한 후에 소비하는 스타일입니다. \n"
                                + "●다만, 너무 아끼기만 하면 삶의 만족도가 떨어질 수 있으니, 가끔은 '예산 안에서의 작은 플렉스'도 허용해 보는 것이 도움이 됩니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("불안/과절약형")
                .description(
                        "●돈을 쓸 때 죄책감이나 불안감이 커서, 지출 자체를 부담스럽게 느끼는 경향이 있을 수 있습니다. \n"
                                + "●소비를 줄이는 대신, 나 자신에게 필요한 것까지 미루거나 포기하는 경우도 생길 수 있습니다. \n"
                                + "●재정적으로 안전한 선 안에서, 나를 위한 최소한의 지출과 휴식은 오히려 장기적인 삶의 효율을 높여 줄 수 있다는 점도 함께 고려해 보면 좋습니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
