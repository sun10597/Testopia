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
 * 학습 스타일 테스트 초기 데이터 세팅
 * - 20문항, 4점 리커트, 총점(0~60점) 기반
 */
@Component
@RequiredArgsConstructor
public class LearningStyleTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "학습 스타일 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        TestEntity test = TestEntity.builder()
                .id(9L)
                .name(TEST_NAME)
                .description("나에게 잘 맞는 공부 방식, 집중 패턴, 자기주도 학습 경향을 알아보는 테스트입니다.")
                .testNum(9) // /test/9 등에 매핑 (겹치면 변경)
                .build();

        List<String> questions = new ArrayList<>();

        // ▣ 학습 태도 / 자기주도성 (1~7)
        questions.add("새로운 내용을 배울 때, 스스로 계획을 세우고 학습 순서를 정하는 편이다.");             // 1
        questions.add("모르는 개념이 나오면, 그냥 넘기지 않고 추가로 검색하거나 찾아보는 편이다.");          // 2
        questions.add("한 번 정한 공부 계획을 웬만하면 끝까지 지키려고 한다.");                          // 3
        questions.add("누가 시키지 않아도, 필요하다고 느끼면 스스로 공부를 시작하는 경우가 많다.");         // 4
        questions.add("공부를 할 때, '왜 이걸 배우는지' 목적을 이해하고 싶어 하는 편이다.");               // 5
        questions.add("시험/평가 외에도, 순수한 호기심으로 공부하는 경험이 있다.");                       // 6
        questions.add("쉽게 포기하기보다, 이해될 때까지 여러 방식으로 접근해 보려 한다.");                 // 7

        // ▣ 학습 방식 / 입력 채널(시각·청각·실습 등) (8~13)
        questions.add("글이나 그림, 도식으로 정리된 자료를 보면 이해가 훨씬 잘 된다.");                   // 8
        questions.add("설명을 듣거나 강의를 통해 내용을 이해하는 것이 비교적 잘 맞는다.");               // 9
        questions.add("직접 써보거나 정리하는 과정에서 머릿속이 정리되는 편이다.");                        // 10
        questions.add("실제로 해보는 실습·프로젝트·예제가 있을 때 가장 잘 배운다고 느낀다.");             // 11
        questions.add("중요한 내용은 나만의 방식으로 다시 요약하거나 노트에 재구성하는 편이다.");           // 12
        questions.add("복습을 할 때, 여러 자료를 섞어보며 스스로 구조를 다시 짜는 편이다.");              // 13

        // ▣ 집중 패턴 / 환경 / 리듬 (14~20)
        questions.add("한 번 집중이 되면, 주변에서 불러도 잘 못 들을 정도로 몰입하는 편이다.");            // 14
        questions.add("짧게 자주 공부하기보다, 한 번에 길게 몰아서 공부하는 편이다.");                   // 15
        questions.add("공부할 때 좋아하는 장소나 루틴(시간대, 음악 등)이 어느 정도 정해져 있다.");         // 16
        questions.add("마감이나 시험이 다가올수록 집중력이 더 올라가는 편이다.");                         // 17
        questions.add("집중이 안 될 때, 그냥 버티기보다 환경이나 방법을 바꾸려고 한다.");                  // 18
        questions.add("공부가 잘 안 되는 날에도, 최소한 조금이라도 '손을 대보는' 편이다.");               // 19
        questions.add("장기적인 목표(시험, 자격증, 이직 준비 등)를 보고 학습 계획을 조정하는 편이다.");      // 20

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
                .resultName("능동형 탐구 학습자")
                .description(
                        "●스스로 계획을 세우고 깊이 파고드는 능동적인 학습자입니다.\n" +
                                "●목표와 맥락을 이해하면서 공부할 때 실력이 크게 올라가는 타입입니다.\n" +
                                "●장기 프로젝트나 심화 학습에 강점을 지니고 있어, 꾸준함을 잘 활용하면 큰 성장을 이룰 수 있습니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("계획형 몰입 학습자")
                .description(
                        "●계획만 세우면 집중해서 해내는 스타일의 학습자입니다.\n" +
                                "●정리·노트·복습 등 기본기를 잘 챙기며, 효율적인 루틴을 선호합니다.\n" +
                                "●계획을 너무 빡빡하게 잡지만 않으면, 부담 없이 오래 가는 공부 패턴을 만들 수 있습니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("안정형 루틴 학습자")
                .description(
                        "●큰 욕심보다는 꾸준함을 추구하는 편에 가까운 학습자입니다.\n" +
                                "●환경이나 루틴이 갖춰지면 그 안에서 안정적으로 공부할 수 있습니다.\n" +
                                "●목표를 조금 더 구체적으로 세우고, 작은 성취를 자주 체크하면 동기부여에 도움이 됩니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("즉흥형 감각 학습자")
                .description(
                        "●필요할 때 몰아서 공부하거나, 흥미가 생길 때 집중하는 스타일입니다.\n" +
                                "●관심이 생기면 빠르게 흡수하지만, 꾸준한 루틴 유지에는 어려움을 느낄 수 있습니다.\n" +
                                "●작은 단위의 목표와 짧은 몰입 시간을 활용하면, 특유의 집중력을 더 잘 살릴 수 있습니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
