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
 * 연애/관계 유형 테스트 초기 데이터 세팅
 * - 20문항, 4점 리커트, 총점(0~60점)으로 결과 구분
 */
@Component
@RequiredArgsConstructor
public class LoveTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    // 20문항 * 3점 = 60점
    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "연애/관계 유형 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        // 1. TestEntity 생성
        TestEntity test = TestEntity.builder()
                .id(5L)
                .name(TEST_NAME)
                .description("사랑을 표현하는 방식, 갈등을 다루는 태도, 나와 잘 맞는 관계 스타일을 알아보는 연애/관계 유형 테스트입니다.")
                .testNum(5) // /test/5 카드에 맞춰 5번으로 설정 (필요하면 변경)
                .build();

        // 2. 질문 리스트 정의 (20문항)
        List<String> questions = new ArrayList<>();

        // ▣ 감정 표현 / 애정 표현 (1~7)
        questions.add("좋아하는 감정이나 고마운 마음을 말이나 행동으로 표현하려고 노력하는 편이다.");        // 1
        questions.add("상대가 힘들어할 때, 무엇을 해주면 좋을지 자연스럽게 떠오르는 편이다.");               // 2
        questions.add("연애를 할 때, 연락 빈도나 표현 방식에 대해 솔직하게 이야기하는 편이다.");             // 3
        questions.add("내가 서운함을 느끼면, 쌓아두기보다 차분하게 풀고 싶어 한다.");                         // 4
        questions.add("사소한 애정 표현(말, 메시지, 작은 행동)이 관계를 지키는 데 중요하다고 생각한다.");      // 5
        questions.add("상대가 나를 좋아해 주는 표현을 할 때, 어색함보다는 따뜻함을 더 많이 느낀다.");          // 6
        questions.add("내 기분이 좋을 때, 그 감정을 상대와 나누고 싶다는 생각이 든다.");                      // 7

        // ▣ 갈등 / 소통 / 존중 (8~13)
        questions.add("의견 차이가 있어도, 일단 상대의 입장을 이해하려고 하는 편이다.");                      // 8
        questions.add("다투더라도 '이 관계를 어떻게 지킬까'를 함께 고민하는 것이 중요하다고 느낀다.");           // 9
        questions.add("연애 중에도 서로가 지켜야 할 '선'과 '예의'가 있다고 믿는다.");                          // 10
        questions.add("상대의 단점이나 부족한 점을 완전히 고치려 하기보다, 어느 정도는 다름으로 받아들인다.");    // 11
        questions.add("화가 나더라도, 일부러 상처 주는 말을 하는 건 피하려고 한다.");                         // 12
        questions.add("감정이 너무 격해졌다고 느끼면, 잠시 시간을 갖고 나중에 다시 이야기하는 편이다.");         // 13

        // ▣ 자율성 / 의존 / 관계 균형 (14~20)
        questions.add("연애를 해도, 서로의 개인 시간과 공간을 존중하는 것이 중요하다고 생각한다.");           // 14
        questions.add("상대에게 모든 감정이나 문제를 의존하기보다, 스스로 해결하려는 부분도 유지하려 한다.");     // 15
        questions.add("연애가 인생의 전부가 되기보다는, 인생의 한 부분으로 건강하게 자리 잡기를 바란다.");       // 16
        questions.add("상대가 나와 다른 취미나 인간관계를 가지는 것을 어느 정도 이해하고 지지하려 한다.");       // 17
        questions.add("관계가 불안할수록 더 집착하기보다는, 왜 불안한지 차분히 들여다보려 한다.");               // 18
        questions.add("이별을 겪더라도, 언젠가는 나에게 더 잘 맞는 관계를 만날 수 있다고 믿으려 한다.");         // 19
        questions.add("연애를 통해 나 자신도 성장하고 싶다는 생각을 종종 한다.");                              // 20

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
     *  - 46~60 : 안정형 동반자 타입
     *  - 31~45 : 공감형 로맨티스트
     *  - 16~30 : 조심스러운 현실형
     *  - 0~15  : 불안/과몰입형
     */
    private List<TestResultTypeEntity> createTestResults(TestEntity test) {

        TestResultTypeEntity r1 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(46)
                .maxScore(MAX_TOTAL_SCORE)
                .resultName("안정형 동반자 타입")
                .description(
                        "●감정 표현과 소통, 자율성의 균형이 잘 잡힌 '성숙한 관계' 지향형입니다. \n"
                                + "●상대와 나를 동시에 존중하며, 문제를 함께 해결해 나가는 동반자 스타일에 가깝습니다. \n"
                                + "●비교적 안정적인 애착을 형성하는 편이며, 건강한 연애를 지속할 가능성이 높습니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("공감형 로맨티스트 타입")
                .description(
                        "●상대방의 감정과 상황에 민감하고, 관계 자체를 소중하게 여기는 유형입니다. \n"
                                + "●때때로 감정에 휩쓸릴 때도 있지만, 기본적으로 배려심이 많고 따뜻한 연애를 추구합니다. \n"
                                + "●자기 경계를 조금 더 단단히 하면, 훨씬 편안한 관계를 만들 수 있습니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("조심스러운 현실형 타입")
                .description(
                        "●상처를 받지 않기 위해 스스로를 한 발 뒤로 빼두는 경향이 있을 수 있는 유형입니다. \n"
                                + "●연애에 과몰입하기보다는 현실적인 기준으로 관계를 바라보는 편이며, 감정 표현이 다소 서툴 수 있습니다. \n"
                                + "●조금 더 솔직한 표현과 믿을 수 있는 사람과의 경험이 쌓이면, 안정적인 관계로 나아갈 수 있습니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("불안/과몰입형 타입")
                .description(
                        "●연애에서 상대의 반응에 크게 흔들리거나, 관계에 지나치게 몰입하는 경향이 있을 수 있는 유형입니다. \n"
                                + "●상대의 행동 하나하나에 의미를 많이 부여하고, 불안과 집착이 동시에 나타날 수 있습니다. \n"
                                + "●관계 외에 나만의 기반과 자존감을 쌓는 시간, 그리고 건강한 경계를 배우는 경험이 중요합니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
