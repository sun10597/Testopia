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
 * 스트레스 대처 스타일 테스트 초기 데이터 세팅
 * - 20문항, 4점 리커트, 총점(0~60점)으로 결과 구분
 */
@Component
@RequiredArgsConstructor
public class StressTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    // 20문항 * 3점 = 60점
    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "스트레스 대처 심리 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        // 1. TestEntity 생성
        TestEntity test = TestEntity.builder()
                .id(3L)
                .name(TEST_NAME)
                .description("스트레스 상황에서 당신이 어떻게 반응하고, 얼마나 빠르게 회복하는지를 알아보는 심리 테스트입니다.")
                .testNum(3) // 다른 테스트와 안 겹치게 번호만 조정
                .build();

        // 2. 질문 리스트 정의 (20문항)
        List<String> questions = new ArrayList<>();

        // ▣ 인지적 대응 능력(1~7)
        questions.add("압박감이 큰 일을 맡게 되면, 우선 해야 할 일들을 쪼개어 계획을 세운다.");              // 1
        questions.add("예상치 못한 문제가 생겨도, 감정적 동요보다 해결 방안을 먼저 찾는다.");                // 2
        questions.add("스트레스 상황에서도, 상황을 객관적으로 바라보려고 노력한다.");                        // 3
        questions.add("실수나 실패가 있을 때, 감정적 반응보다는 원인을 분석하는 편이다.");                    // 4
        questions.add("복잡한 일을 처리할 때, 우선순위를 재정비하여 부담을 줄이려 한다.");                    // 5
        questions.add("스트레스가 쌓여도, 해야 할 일을 미루기보다 조금씩이라도 처리한다.");                    // 6
        questions.add("힘든 상황에서도, 나에게 주어진 역할을 유지하려는 편이다.");                            // 7

        // ▣ 감정 조절·표현 능력(8~13)
        questions.add("감정이 격해지는 순간에도, 한 번 멈추고 생각하려는 습관이 있다.");                      // 8
        questions.add("마음이 불안할 때, 내가 안정될 수 있는 말이나 행동을 떠올린다.");                        // 9
        questions.add("누군가와 갈등이 생기면, 감정적 폭발보다는 대화를 선택하려 한다.");                      // 10
        questions.add("스트레스를 받는 상황에서도, 누군가에게 쉽게 공격적이 되지 않는다.");                    // 11
        questions.add("기분이 나빠도, 일상적인 표정이나 태도를 유지하려고 노력한다.");                        // 12
        questions.add("감정적으로 힘들어도, 그 감정을 관리할 방법을 알고 있다.");                              // 13

        // ▣ 회복 탄력성·루틴(14~20)
        questions.add("하루가 힘들었던 날에도, 나만의 회복 루틴이 있다.");                                   // 14
        questions.add("지친 상태에서도, 잠·식사 같은 기본적인 생활 습관을 유지한다.");                        // 15
        questions.add("스트레스를 받는 기간이 길어져도, 완전히 무너지는 일은 드물다.");                        // 16
        questions.add("에너지가 떨어졌을 때, 나를 충전시킬 취미나 활동이 있다.");                              // 17
        questions.add("일정 기간이 지나면, 자연스럽게 컨디션이 회복되는 편이다.");                             // 18
        questions.add("힘든 일이 생기면, 주변에 조언하거나 도움을 요청할 대상을 떠올릴 수 있다.");              // 19
        questions.add("스트레스 경험을 지나고 나면, 그 과정에서 배운 점을 스스로 정리하려 한다.");              // 20

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
        System.err.println(TEST_NAME + " 초기 데이터(20문항, 4유형)가 성공적으로 저장되었습니다.");
    }

    /**
     * 질문 한 개와 4개의 선택지를 생성하는 헬퍼 메서드
     * (모든 문항 정방향 코딩: 긍정적 대처 능력이 높을수록 점수↑)
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
     *  - 46~60 : 회복탄력 심화형
     *  - 31~45 : 안정적 대처형
     *  - 16~30 : 요동치는 감정형
     *  - 0~15  : 소진 위험형
     */
    private List<TestResultTypeEntity> createTestResults(TestEntity test) {

        TestResultTypeEntity r1 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(46)
                .maxScore(MAX_TOTAL_SCORE)
                .resultName("회복탄력 심화형")
                .description("●스트레스 상황에서도 스스로를 잘 추스르고, 금방 다시 중심을 잡는 유형입니다. \n"
                        + "●문제를 구조적으로 파악하고, 감정과 상황을 적절히 분리해 생각할 수 있습니다. \n"
                        + "●주변 사람들에게도 '멘탈이 강한 사람'으로 보일 가능성이 높습니다.")
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("안정적 대처형")
                .description("●스트레스를 받더라도 기본적인 생활 리듬과 업무 수행을 유지하는 편입니다. \n"
                        + "●가끔은 흔들릴 수 있지만, 시간을 두고 스스로 회복하는 힘이 있습니다. \n"
                        + "●조금 더 의도적으로 휴식과 감정 정리를 챙긴다면 더욱 단단해질 수 있습니다.")
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("요동치는 감정형")
                .description("●감정 기복이 비교적 크고, 스트레스 상황에서 쉽게 에너지가 떨어질 수 있는 유형입니다. \n"
                        + "●반면 타인의 감정에도 민감하여 공감 능력이 뛰어난 면이 있습니다. \n"
                        + "●혼자 버티려 하기보다, 도움을 요청하는 연습과 작은 루틴을 만드는 것이 도움이 됩니다.")
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("소진 위험형")
                .description("●최근 스트레스가 많이 쌓여 있거나, 회복 에너지가 크게 떨어져 있을 가능성이 높은 유형입니다. \n"
                        + "●의욕 저하, 무기력감, 작은 일에도 예민해지는 경험을 자주 할 수 있습니다. \n"
                        + "●지금은 '더 잘해야지'보다, 충분한 휴식과 주변의 지지, 전문적인 도움을 검토해 보는 것이 중요합니다.")
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
