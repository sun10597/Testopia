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
 * 직업 적성 테스트 초기 데이터 세팅
 * - 20문항, 4점 리커트, 총점(0~60점)으로 결과 구분
 */
@Component
@RequiredArgsConstructor
public class CareerTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    // 20문항 * 3점 = 60점
    private static final int MAX_TOTAL_SCORE = 60;
    private static final String TEST_NAME = "직업 적성 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너뜀.");
            return;
        }

        // 1. TestEntity 생성
        TestEntity test = TestEntity.builder()
                .id(4L)
                .name(TEST_NAME)
                .description("나의 성향과 잘 맞는 직업 분야와 업무 환경을 탐색하는 직업 적성 심리 테스트입니다.")
                .testNum(4) // 기존 테스트 번호랑만 안 겹치게 설정
                .build();

        // 2. 질문 리스트 정의 (20문항)
        List<String> questions = new ArrayList<>();

        // ▣ 업무 스타일 / 일하는 방식 (1~7)
        questions.add("스스로 목표를 정하고, 그 목표를 향해 일을 추진하는 것이 잘 맞는 편이다.");          // 1
        questions.add("반복적인 업무보다는, 새로운 과제나 변화를 다루는 일을 선호한다.");                 // 2
        questions.add("일을 할 때, '왜 이 일을 하는지' 목적을 이해하고 움직이는 편이다.");                // 3
        questions.add("복잡한 문제를 해결하거나 구조를 정리하는 업무에 흥미를 느낀다.");                   // 4
        questions.add("성과가 숫자나 결과로 명확히 드러나는 일을 하면 동기부여가 된다.");                   // 5
        questions.add("주어진 지시만 수행하는 것보다, 어떻게 하면 더 잘할 수 있을지 제안하는 편이다.");      // 6
        questions.add("여러 업무를 동시에 조율하고 우선순위를 정하는 일을 부담스럽기보다 재미있게 느낀다.");  // 7

        // ▣ 사람 / 협업 / 커뮤니케이션 (8~13)
        questions.add("사람들과 함께 의견을 나누며 일하는 과정에서 에너지를 얻는 편이다.");                // 8
        questions.add("타인의 요구나 상황을 파악하고, 그에 맞게 소통 방식을 조정하는 편이다.");             // 9
        questions.add("회의나 발표 자리에서 나의 생각을 정리해 말하는 것이 크게 두렵지 않다.");             // 10
        questions.add("고객·사용자·동료 등 '사람'과 직접 마주하는 일을 어느 정도 즐기는 편이다.");          // 11
        questions.add("팀의 분위기나 관계가 좋을수록, 나의 업무 효율도 올라가는 편이다.");                 // 12
        questions.add("다른 사람의 장단점을 보고, 누가 어떤 일을 맡으면 좋을지 떠올리는 편이다.");           // 13

        // ▣ 성장 지향 / 도전 성향 / 환경 선호 (14~20)
        questions.add("새로운 기술이나 지식을 배우는 것에 거부감보다 호기심이 더 크다.");                 // 14
        questions.add("불확실성이 있더라도, 성장 기회가 크다면 도전해보고 싶다는 생각이 든다.");           // 15
        questions.add("안정적인 환경도 좋지만, 어느 정도는 변화와 자극이 있어야 재미를 느낀다.");            // 16
        questions.add("내가 하는 일이 사회나 타인에게 어떤 영향을 주는지 중요하게 생각한다.");             // 17
        questions.add("커리어를 설계할 때, 연봉·안정성뿐 아니라 나와 맞는 '업무 분위기'도 고려한다.");        // 18
        questions.add("지금보다 더 나에게 맞는 직무나 산업이 있다면, 장기적으로 이동을 고민해볼 의향이 있다."); // 19
        questions.add("나의 강점과 약점을 바탕으로, 어울리는 일을 찾고 싶다는 생각을 자주 한다.");           // 20

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
     *  - 46~60 : 도전·창의형
     *  - 31~45 : 성장·전문형
     *  - 16~30 : 안정·조화형
     *  - 0~15  : 재정비·탐색형
     */
    private List<TestResultTypeEntity> createTestResults(TestEntity test) {

        TestResultTypeEntity r1 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(46)
                .maxScore(MAX_TOTAL_SCORE)
                .resultName("도전·창의형 커리어 타입")
                .description(
                        "●새로운 과제와 변화를 즐기며, 자율성과 성장 기회를 중요하게 여기는 유형입니다. \n"
                                + "●스타트업, 기획·전략, 신사업, 크리에이티브 직군처럼 자유로운 환경에서 잠재력이 잘 발휘될 수 있습니다."
                )
                .build();

        TestResultTypeEntity r2 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(31)
                .maxScore(45)
                .resultName("성장·전문형 커리어 타입")
                .description(
                        "●체계적인 환경에서 전문성을 쌓아가는 것을 선호하는 유형입니다. \n"
                                + "●IT 개발, 데이터 분석, 전문직, 대기업/중견기업 등에서 경력을 깊이 있게 쌓는 방식이 잘 맞을 수 있습니다."
                )
                .build();

        TestResultTypeEntity r3 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(16)
                .maxScore(30)
                .resultName("안정·조화형 커리어 타입")
                .description(
                        "●예측 가능한 업무와 안정적인 환경을 선호하며, 관계와 팀워크를 중요하게 보는 유형입니다. \n"
                                + "●공공기관, 행정·사무, 지원·운영 직무, 안정적인 조직에서 강점을 발휘하기 좋습니다."
                )
                .build();

        TestResultTypeEntity r4 = TestResultTypeEntity.builder()
                .test(test)
                .minScore(0)
                .maxScore(15)
                .resultName("재정비·탐색형 커리어 타입")
                .description(
                        "●현재는 '나와 맞는 일'을 찾는 과정에 있거나, 커리어 방향에 대한 확신이 약할 수 있는 시기입니다. \n"
                                + "●여러 직무를 폭넓게 체험해 보고, 자신의 가치관·강점을 정리하는 시간이 도움이 될 수 있습니다."
                )
                .build();

        return List.of(r1, r2, r3, r4);
    }
}
