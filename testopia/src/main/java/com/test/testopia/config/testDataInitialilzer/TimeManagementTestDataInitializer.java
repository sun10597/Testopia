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
 * 시간관리/우선순위 테스트 초기 데이터 세팅
 */
@Component
@RequiredArgsConstructor
public class TimeManagementTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "시간관리/우선순위 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        TestEntity test = TestEntity.builder()
                .id(11L)
                .name(TEST_NAME)
                .description("나의 시간 관리 방식, 우선순위 설정 습관, 마감 대응 스타일을 알아보는 테스트입니다.")
                .testNum(11)
                .build();

        List<String> questions = new ArrayList<>();

        // ▣ 계획 수립 / 일정 관리 (1~7)
        questions.add("하루를 시작할 때, 해야 할 일을 머릿속이나 메모로 정리하는 편이다.");              // 1
        questions.add("일이 여러 개 생기면, 우선순위를 나름대로 정해서 처리하는 편이다.");                  // 2
        questions.add("중요한 일정은 캘린더나 메모 앱 등에 기록해 두는 습관이 있다.");                     // 3
        questions.add("하루·일주일 단위로 대략적인 계획을 세우는 것이 익숙하다.");                          // 4
        questions.add("갑자기 생긴 일에도 기존 일정을 완전히 무너뜨리기보다는 조정하려 한다.");              // 5
        questions.add("해야 할 일을 미루면 나중에 더 힘들어진다는 것을 잘 알고 있다.");                      // 6
        questions.add("내 에너지 상태(아침/저녁 등)에 맞춰 중요한 일을 배치하려고 한다.");                   // 7

        // ▣ 집중 / 방해 요소 관리 (8~13)
        questions.add("집중해야 할 때, 휴대폰 알림이나 방해 요소를 줄이려는 편이다.");                       // 8
        questions.add("작업 중에 다른 생각이 나도, 끝까지 한 가지를 마무리하려고 한다.");                    // 9
        questions.add("SNS·영상 등으로 시간을 더 쓰고 있는 건 아닌지 가끔 점검해 본다.");                   // 10
        questions.add("예정에 없던 약속이나 제안이 들어왔을 때, 일정과 에너지를 고려해 수락/거절을 결정한다."); // 11
        questions.add("하기 싫은 일도 일정 시간만이라도 '손을 대보면' 의외로 진행이 되는 경험을 안다.");       // 12
        questions.add("집중이 깨졌을 때, 금방 다시 원래 작업으로 돌아올 수 있는 편이다.");                   // 13

        // ▣ 마감 / 실행력 / 밸런스 (14~20)
        questions.add("마감이 가까워질수록, 오히려 집중력이 올라가는 편이다.");                            // 14
        questions.add("여러 일을 동시에 벌리기보다, 끝낸 후 다음 일을 시작하려고 한다.");                    // 15
        questions.add("일·공부·휴식·여가 시간의 균형에 대해 자주 생각해 본다.");                            // 16
        questions.add("스스로 정한 마감도 어느 정도 지키려고 노력한다.");                                  // 17
        questions.add("중요한 일을 급한 일보다 우선순위에 두는 편이다.");                                   // 18
        questions.add("계획대로 다 못 했더라도, 무엇을 했고 무엇을 못 했는지 정리해 보는 편이다.");          // 19
        questions.add("장기적인 목표(진로, 건강, 재정 등)를 위해 지금 시간을 어떻게 써야 할지 고민한다.");      // 20

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
                .resultName("전략적 플래너 타입")
                .description(
                        "●시간과 에너지를 설계하는 능력이 뛰어난 유형입니다.\n" +
                                "●중요한 일을 앞에 두고, 우선순위를 잘 조정하며 일과 휴식의 균형도 고려합니다.\n" +
                                "●가끔은 계획에 너무 얽매이지 않고, 유연함을 조금 더 허용해도 충분히 잘 해낼 수 있습니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("실용적 실행가 타입")
                .description(
                        "●필요한 계획은 세우되, 결국 중요한 것은 '실행'이라고 생각하는 유형입니다.\n" +
                                "●해야 할 일을 적당한 선에서 처리하며, 상황에 따라 우선순위를 조정할 줄 압니다.\n" +
                                "●조금만 더 기록과 복기를 추가하면, 시간 사용의 효율이 한 단계 더 올라갈 수 있습니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("유연한 즉흥형 타입")
                .description(
                        "●계획보다는 흐름과 기분에 따라 움직이는 편인 유형입니다.\n" +
                                "●마감이 가까워져야 집중이 잘 되거나, 당겨지는 일부터 처리하는 경향이 있습니다.\n" +
                                "●작은 루틴(매일 10~20분, 하루 3개 할 일 등)부터 도입하면, 부담 없이 시간 관리 능력을 키울 수 있습니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("타임 크런치형 타입")
                .description(
                        "●계획을 세우기보다는, 일이 닥친 뒤에 몰아서 처리하는 패턴이 반복될 수 있는 유형입니다.\n" +
                                "●단기적인 집중력은 좋지만, 장기적인 피로와 스트레스를 누적시키기 쉽습니다.\n" +
                                "●아주 작은 단위의 계획부터 연습해 보면, 시간에 쫓기지 않는 날들이 점점 늘어날 수 있습니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
