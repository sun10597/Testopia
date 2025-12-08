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
 * 리더십 유형 테스트 초기 데이터 세팅
 */
@Component
@RequiredArgsConstructor
public class LeadershipTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "리더십 유형 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        TestEntity test = TestEntity.builder()
                .id(10L)
                .name(TEST_NAME)
                .description("팀 작업, 프로젝트, 조직 속에서 내가 발휘하는 리더십 스타일을 알아보는 테스트입니다.")
                .testNum(10)
                .build();

        List<String> questions = new ArrayList<>();

        // ▣ 주도성 / 방향 제시 (1~7)
        questions.add("팀 작업에서 자연스럽게 방향을 잡거나 의견을 정리하는 역할을 맡는 편이다.");       // 1
        questions.add("문제가 생기면 누가 시키지 않아도 먼저 나서서 해결책을 찾으려 한다.");           // 2
        questions.add("목표와 우선순위를 정리해 팀원들과 공유하는 것에 익숙하다.");                      // 3
        questions.add("회의나 논의 자리에서 침묵보다는 의견을 제시하는 편에 가깝다.");                  // 4
        questions.add("팀이 흔들릴 때, '우리가 왜 이 일을 하는지' 다시 상기시키는 사람이 되곤 한다.");   // 5
        questions.add("새로운 시도나 변화를 제안하는 것이 두렵기보다 기대되는 편이다.");               // 6
        questions.add("내가 먼저 움직이면 주변도 자연스럽게 따라오는 경험을 한 적이 있다.");           // 7

        // ▣ 소통 / 피드백 / 관계 관리 (8~13)
        questions.add("팀원들의 강점과 약점을 보면서, 역할 배분에 대해 생각해 본 적이 많다.");         // 8
        questions.add("피드백을 줄 때, 상대의 기분과 성장 모두를 고려하려고 한다.");                  // 9
        questions.add("갈등 상황에서 한쪽 편을 들기보다, 중간에서 조율해 보려는 편이다.");             // 10
        questions.add("팀원의 이야기를 듣고 공감해 주는 것이 자연스럽다.");                           // 11
        questions.add("칭찬과 인정이 팀 분위기를 살리는 데 얼마나 중요한지 알고 있다.");                // 12
        questions.add("내가 분위기를 어떻게 잡느냐에 따라 팀의 에너지가 달라지는 것을 느낀 적이 있다.");   // 13

        // ▣ 책임감 / 실행력 / 성장 지향 (14~20)
        questions.add("결과가 좋지 않아도, 책임을 회피하기보다 이유를 함께 찾으려 한다.");              // 14
        questions.add("맡은 일은 어떻게든 끝까지 해내야 한다는 생각이 강한 편이다.");                   // 15
        questions.add("내가 부족한 부분은 배우고 채워가면서 리더 역할을 이어가는 편이다.");              // 16
        questions.add("팀의 성공을 내 성과처럼 기쁘게 느낀다.");                                        // 17
        questions.add("팀원들의 성장을 돕는 것이 리더의 중요한 역할이라고 믿는다.");                    // 18
        questions.add("리더십은 타고나는 것보다 '성장시키는 것'에 가깝다고 생각한다.");                  // 19
        questions.add("리더가 아니어도, 팀의 흐름과 결과에 책임감을 느끼는 편이다.");                    // 20

        for (int i = 0; i < questions.size(); i++) {
            test.addQuestion(createQuestion(i + 1, questions.get(i)));
        }

        List<TestResultTypeEntity> results = createTestResults(test);
        results.forEach(test::addResultType);

        testRepository.save(test);
        System.out.println(TEST_NAME + " 데이터(20문항, 4유형) 초기화 완료");
    }

    private QuestionEntity createQuestion(int orderNo, String text) {
        QuestionEntity question = QuestionEntity.builder()
                .orderNo(orderNo)
                .text(text)
                .build();

        question.addChoice(ChoiceEntity.builder().orderNo(1).text(TEXT_VERY_MUCH).score(3).build());
        question.addChoice(ChoiceEntity.builder().orderNo(2).text(TEXT_SOMEWHAT).score(2).build());
        question.addChoice(ChoiceEntity.builder().orderNo(3).text(TEXT_NOT_MUCH).score(1).build());
        question.addChoice(ChoiceEntity.builder().orderNo(4).text(TEXT_NOT_AT_ALL).score(0).build());

        return question;
    }

    private List<TestResultTypeEntity> createTestResults(TestEntity test) {

        TestResultTypeEntity r1 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(46)
                .maxScore(MAX_TOTAL_SCORE)
                .resultName("비전형 리더 타입")
                .description(
                        "●방향을 제시하고 사람들을 이끄는 데 강점을 지닌 유형입니다.\n" +
                                "●목표를 설정하고, 팀이 나아갈 길을 설계하는 데에서 활약합니다.\n" +
                                "●가끔은 혼자 너무 많은 책임을 지려 하기보다, 역할을 나누는 연습도 도움이 됩니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("조율형 리더 타입")
                .description(
                        "●사람과 사람 사이를 이어 주고, 팀의 균형을 맞추는 리더십입니다.\n" +
                                "●갈등을 조정하고, 각자의 강점을 살릴 수 있도록 분위기를 만드는 데 능합니다.\n" +
                                "●의견을 모으는 것에서 한 걸음 더 나아가, 결정과 실행을 이끄는 경험을 쌓으면 좋습니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("지원형 팀플레이어 타입")
                .description(
                        "●앞에 나서기보다는, 옆에서 팀을 든든하게 받쳐주는 스타일입니다.\n" +
                                "●주어진 역할을 책임감 있게 수행하며, 리더를 돕는 위치에서 강점을 발휘합니다.\n" +
                                "●때로는 작은 영역이라도 주도권을 가져보면, 자신의 리더십 자원을 더 잘 발견할 수 있습니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("관망형 멤버 타입")
                .description(
                        "●팀의 흐름을 지켜보며 상황에 맞게 따라가는 편에 가까운 유형입니다.\n" +
                                "●리더십을 억지로 가져가기보다, 맡겨진 역할 안에서 안정감을 느끼는 편입니다.\n" +
                                "●작은 선택부터 의견을 내고 책임지는 연습을 하면, 자신만의 리더십 스타일을 만들 수 있습니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
