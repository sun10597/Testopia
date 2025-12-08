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
import java.util.List;
import java.util.ArrayList;

/**
 * 애플리케이션 시작 시 DB에 초기 테스트 데이터를 삽입하는 클래스입니다.
 * (테토-에겐 성향 테스트 데이터 20문항, 60점 만점)
 * - 극단적 에겐 편중 문제를 해결하기 위해 문항 11~20은 역코딩(에겐 성향 측정) 적용
 */
@Component
@RequiredArgsConstructor
public class TettoTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;


    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    // 최대 총점: 20문항 * 3점/문항 = 60점
    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "테토/에겐 성향 테스트";
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너김.");
            return;
        }

        // ===== 1. 테스트 생성 (TestEntity) =====
        TestEntity test = TestEntity.builder()
                .id(2L)
                .name(TEST_NAME)
                .description("당신의 논리/분석(테토) 성향과 공감/관계(에겐) 성향을 알아보는 테스트입니다.")
                .testNum(2)
                .build();

        // ----------------------------------------------------------------------
        // 질문 목록 (20개) 정의: 1~10번은 테토 질문, 11~20번은 에겐 질문 (역코딩 대상)
        // ----------------------------------------------------------------------
        List<String> questions = new ArrayList<>();

        // 1~10번: 테토 성향 측정 (테토 응답 = 3점)
        questions.add("누군가 고민을 이야기할 때, 나는 감정적 공감보다 논리적인 해결책 제시가 더 중요하다고 생각한다."); // 1
        questions.add("새로운 환경이나 사람을 만날 때, 정량적인 정보(규칙, 매뉴얼 등)가 심리적 안정감을 준다."); // 2
        questions.add("어떤 일을 계획할 때, 가장 효율적인 동선과 시간 배분을 짜는 것에 많은 시간을 할애한다."); // 3
        questions.add("단체 활동에서 이견이 충돌할 때, 객관적인 데이터와 논리적 근거로 설득하는 것이 가장 빠르다."); // 4
        questions.add("스트레스를 해소할 때, 나는 논리 퍼즐이나 기술 습득 등 문제 해결 활동에 몰두하는 편이다."); // 5
        questions.add("나의 업무나 결과에 대해 비판을 받았을 때, 비판의 합리성을 판단하는 것이 감정을 추스르는 것보다 먼저이다."); // 6
        questions.add("가장 흥미로운 콘텐츠 장르는 정보 분석, 경제, 법정 등 객관적인 사실을 다루는 것이다."); // 7
        questions.add("나는 물건을 정리할 때, 사용 빈도와 기능testNum별 효율성을 기준으로 명확하게 분류한다."); // 8
        questions.add("약속 시간에 늦은 상대방에게 다음부터 늦지 않을 방법을 제안하는 편이다."); // 9
        questions.add("나의 주된 관심사는 이론의 타당성이나 기술적 문제 해결에 대한 깊은 토론이다."); // 10

        // 11~20번: 에겐 성향 측정 (에겐 응답 = 3점, 역코딩 적용)
        questions.add("나는 복잡한 문제를 분석하기보다, 당사자의 감정을 먼저 살피고 정서적 지지를 해주는 것이 중요하다고 믿는다."); // 11 (원래 1번 문항의 역코딩 형태)
        questions.add("조직 내에서 규칙보다 사람들의 사기나 관계적 분위기가 업무 효율에 더 큰 영향을 미친다고 생각한다."); // 12 (원래 11번 문항의 역코딩 형태)
        questions.add("미래를 계획할 때, 정량적인 예측보다 사람들과의 관계나 시대적 흐름 등 직관적 요소에 더 크게 의존한다."); // 13 (원래 13번 문항의 역코딩 형태)
        questions.add("타인에게 조언을 할 때, 나는 냉철한 피드백보다 따뜻한 위로와 공감을 제공하는 것이 진정으로 돕는 길이라 생각한다."); // 14 (원래 19번 문항의 역코딩 형태)
        questions.add("나는 물건을 정리할 때, 효율성보다 애착이나 추억 등 감성적 기준을 중요하게 생각한다."); // 15 (원래 8번 문항의 역코딩 형태)
        questions.add("나는 논리적인 주장으로 갈등을 해결하기보다, 관계의 조화를 위해 불필요한 논쟁 자체를 피하려 한다."); // 16 (원래 12번 문항의 역코딩 형태)
        questions.add("주말 여가 시간에는 계획 없이 즉흥적으로 행동할 때 가장 편안함과 즐거움을 느낀다."); // 17 (원래 18번 문항의 역코딩 형태)
        questions.add("나의 삶의 목표는 명확한 성과보다, 주변 사람들과의 깊은 정서적 교류를 통해 행복을 쌓는 것이다."); // 18 (원래 20번 문항의 역코딩 형태)
        questions.add("나에게 구체적인 근거와 자료보다, 진심을 담은 감정적인 호소가 더 효과적인 설득 방법이다."); // 19 (원래 17번 문항의 역코딩 형태)
        questions.add("세상이 더 나아지기 위해서는 과학 기술보다 인간의 윤리와 공감 능력의 발전이 가장 중요하다고 믿는다."); // 20 (원래 15번 문항의 역코딩 형태)


        // 질문과 선택지 추가
        for (int i = 0; i < questions.size(); i++) {
            // 1~10번은 테토 코딩 (3점=테토 강함), 11~20번은 에겐 코딩 (3점=에겐 강함)
            if (i < 10) {
                // 1~10번: 정방향 코딩 (테토=3점)
                test.addQuestion(createQuestion(i + 1, questions.get(i), false));
            } else {
                // 11~20번: 역방향 코딩 (에겐=3점, 즉 테토=0점)
                test.addQuestion(createQuestion(i + 1, questions.get(i), true));
            }
        }

        // ===== 2. 결과 유형 추가 (심화형/우위형 이름 적용) =====
        List<TestResultTypeEntity> results = createTestResults(test);

        for (TestResultTypeEntity result : results) {
            test.addResultType(result);
        }

        // ===== 3. 저장 =====
        testRepository.save(test);
        System.err.println(TEST_NAME + "테토-에겐 성향 테스트 데이터 20문항과 4가지 결과 유형이 성공적으로 저장되었습니다.");
    }

    /**
     * 질문 엔티티와 선택지 4개를 생성하여 반환하는 헬퍼 메서드.
     * @param isReverseCoding true면 에겐 성향이 3점(테토 점수 0점)이 되도록 점수를 역코딩
     */
    private QuestionEntity createQuestion(int orderNo, String text, boolean isReverseCoding) {
        QuestionEntity question = QuestionEntity.builder()
                .orderNo(orderNo)
                .text(text)
                .build();

        // isReverseCoding에 따라 점수 배치를 역순으로 변경
        if (!isReverseCoding) {
            // 정방향 코딩 (테토 성향이 강함 = 3점)
            question.addChoice(ChoiceEntity.builder().orderNo(1).text(TEXT_VERY_MUCH).score(3).build());
            question.addChoice(ChoiceEntity.builder().orderNo(2).text(TEXT_SOMEWHAT).score(2).build());
            question.addChoice(ChoiceEntity.builder().orderNo(3).text(TEXT_NOT_MUCH).score(1).build());
            question.addChoice(ChoiceEntity.builder().orderNo(4).text(TEXT_NOT_AT_ALL).score(0).build());
        } else {
            // 역방향 코딩 (에겐 성향이 강함 = 3점, 즉 테토 점수 0점)
            question.addChoice(ChoiceEntity.builder().orderNo(1).text(TEXT_VERY_MUCH).score(0).build());
            question.addChoice(ChoiceEntity.builder().orderNo(2).text(TEXT_SOMEWHAT).score(1).build());
            question.addChoice(ChoiceEntity.builder().orderNo(3).text(TEXT_NOT_MUCH).score(2).build());
            question.addChoice(ChoiceEntity.builder().orderNo(4).text(TEXT_NOT_AT_ALL).score(3).build());
        }

        return question;
    }

    private List<TestResultTypeEntity> createTestResults(TestEntity test) {

        // 1. 테토 심화형 (Systematic Thinker) - 46점 ~ 60점
        TestResultTypeEntity r1 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(46)
                .maxScore(MAX_TOTAL_SCORE)
                .resultName("테토 심화형")
                .description(
                        "●당신은 철저한 논리와 분석을 기반으로 세상을 바라보는 전략가입니다. \n"
                         +"●문제 상황에서도 감정에 휘둘리지 않고 핵심을 정확히 파악해 해결책을 제시합니다. \n"
                                +"●다만 상대의 감정 신호를 놓치면 관계에서 거리감이 생길 수 있으니, 가끔은 따뜻한 한마디가 큰 힘이 될 수 있습니다."
                )

                .build();

        // 2. 테토 우위형 (Balanced Analyst) - 31점 ~ 45점
        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("테토 우위형")
                .description(
                        "●이성적인 판단과 논리적 사고가 우선인 사람입니다.\n" +
                                "●효율적인 선택을 선호하면서도, 필요할 때는 관계의 중요성도 고려하려 노력합니다.\n" +
                                "●균형 잡힌 사고 덕분에 다양한 상황에서 합리적인 결정을 내릴 수 있습니다."
                )

                .build();

        // 3. 에겐 우위형 (Empathetic Communicator) - 16점 ~ 30점
        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("에겐 우위형")
                .description(
                        "●타인의 감정과 분위기를 빠르게 읽어내는 공감형 성향을 지녔습니다.\n" +
                                "●상대가 어떤 감정을 느끼는지 자연스럽게 이해하며, 주변 사람에게 편안함을 주는 스타일입니다.\n" +
                                "●다만 감정에 치우치면 결정을 미루거나 지칠 수 있어, 때로는 명확한 기준 설정이 도움이 됩니다."
                )

                .build();

        // 4. 에겐 심화형 (Relational Intuitive) - 0점 ~ 15점
        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("에겐 심화형")
                .description(
                        "●정서적 유대와 관계의 조화를 무엇보다 중요하게 여기는 사람입니다.\n" +
                                "●타인의 마음을 잘 돌보고 따뜻한 분위기를 만드는 데 탁월합니다.\n" +
                                "●그러나 갈등이나 논쟁을 피하려다 자신의 의견이 흐려질 수 있어, '나의 기준'을 지키는 연습이 필요합니다."
                )

                .build();

        return List.of(r1, r2, r3, r4);
    }
}