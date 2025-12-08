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
 * 유머 감각 테스트 초기 데이터 세팅
 * - 20문항, 4점 리커트, 총점(0~60점) 기반 결과 유형 판단
 */
@Component
@RequiredArgsConstructor
public class HumorSenseTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "유머 감각 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        TestEntity test = TestEntity.builder()
                .id(8L)
                .name(TEST_NAME)
                .description("나만의 유머 코드, 웃음 포인트, 말투와 상황 반응까지 알아보는 유머 성향 분석 테스트입니다.")
                .testNum(8) // /test/8 카드 링크와 매칭
                .build();

        List<String> questions = new ArrayList<>();

        // ▣ 언어유희/말장난 감성 (1~7)
        questions.add("말의 뉘앙스나 단어의 중의성을 활용한 개그가 특히 재미있다."); // 1
        questions.add("누군가 말실수를 했을 때, 머릿속으로 다양한 말장난이 떠오르는 편이다."); // 2
        questions.add("평범한 대화에서도 말 끝이나 단어를 꼬아서 개그로 바꾸고 싶어질 때가 있다."); // 3
        questions.add("유머는 상황보다 '표현 방식'이 더 중요하다고 생각한다."); // 4
        questions.add("언어 센스나 드립 타이밍이 좋은 사람에게 호감이 생긴다."); // 5
        questions.add("친한 사람들 사이에서는 말로 분위기를 띄우는 역할을 맡는 편이다."); // 6
        questions.add("생각보다 말보다, 말장난을 던지는 순간을 더 즐긴다."); // 7

        // ▣ 상황/반전/디테일 관찰형 개그 (8~13)
        questions.add("일상 속 사소한 상황에서도 웃긴 포인트를 잘 발견하는 편이다."); // 8
        questions.add("특정 상황에서 누군가의 반응이나 표정이 더 웃길 때가 많다."); // 9
        questions.add("남들이 지나치는 디테일을 캐치해 웃음 코드로 발전시키는 편이다."); // 10
        questions.add("예상치 못한 타이밍의 한마디가 가장 큰 웃음을 만든다고 생각한다."); // 11
        questions.add("상황극이나 상황 묘사 개그에 크게 공감한다."); // 12
        questions.add("영화/예능 속 상황 개그에 빠져드는 편이다."); // 13

        // ▣ 허무·병맛·초현실 유머 감각 (14~20)
        questions.add("맥락이 없거나 갑작스러운 개그에 터질 때가 많다."); // 14
        questions.add("뇌가 잠깐 멈칫하는 어이없는 개그가 더 재밌다."); // 15
        questions.add("논리적으로 설명이 안 되는 개그에도 매력을 느낀다."); // 16
        questions.add("실소가 나오는 허무 개그나 병맛 포인트를 빠르게 캐치한다."); // 17
        questions.add("예상 가능한 개그보다 전혀 예상 못 한 전개가 더 시원하다."); // 18
        questions.add("친한 친구들끼리만 통하는 '흐름 없는 개그'가 좋다."); // 19
        questions.add("가끔 내가 무슨 말 하고 웃는지 나도 모를 때가 있다."); // 20

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
                .resultName("폭발적 드립러 타입")
                .description(
                        "●웃음 코드가 독창적이고 즉흥적인 편입니다.\n" +
                                "●말장난이든 상황 개그든 자신만의 감각으로 분위기를 압도할 수 있습니다.\n" +
                                "●가끔은 남들이 따라오지 못해 '나만 웃는 상황'이 생길 수 있으니 상황 조절 능력이 빛을 발합니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("센스형 공감 유머러")
                .description(
                        "●상황을 잘 캐치하고, 적절한 타이밍에 웃음을 던지는 스타일입니다.\n" +
                                "●과하지 않으면서도 존재감 있는 개그 감각을 지녔습니다.\n" +
                                "●다만 타이밍을 놓치면 유머가 묻히는 경우가 있으니, 자신감을 유지하면 더 빛납니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("조용한 관찰 유머형")
                .description(
                        "●겉으로 튀진 않지만, 상황 속 숨은 웃음 포인트를 잘 발견하는 사람입니다.\n" +
                                "●혼자 웃거나 소수 인원과 공유할 때 더 매력적입니다.\n" +
                                "●센스는 있지만 큰 장면에서 웃길 용기가 부족해 보일 수 있으니, 타이밍 감각을 조금만 더 믿어도 좋습니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("반응형 리액션 웃음러")
                .description(
                        "●유머를 '만드는' 사람보다는 '반응'하며 끌어올리는 스타일입니다.\n" +
                                "●웃음소리, 표정, 반응이 주변을 즐겁게 만듭니다.\n" +
                                "●다만 웃음 포인트가 상대에게 좌우될 수 있어, 자신만의 취향을 찾으면 훨씬 더 유쾌해질 수 있습니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
