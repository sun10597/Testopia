package com.test.testopia.config;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MbtiTestDataInitializer implements CommandLineRunner {

    private final TestRepository testRepository;

    private static final String TEXT_VERY_MUCH = "매우 그렇다";
    private static final String TEXT_SOMEWHAT = "어느 정도 그렇다";
    private static final String TEXT_NOT_MUCH = "그렇지 않은 편이다";
    private static final String TEXT_NOT_AT_ALL = "전혀 그렇지 않다";

    private static final int QUESTIONS_PER_AXIS = 10;
    private static final int MAX_SCORE_PER_AXIS = QUESTIONS_PER_AXIS * 3; // 30점

    private static final String TEST_NAME = "MBTI 40문항 성격 유형 테스트";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (testRepository.findByName(TEST_NAME).isPresent()) {
            System.out.println(TEST_NAME + " 데이터가 이미 존재합니다. 초기화 건너김.");
            return;
        }

        // ===== 1. 테스트 생성 (TestEntity) =====
        TestEntity test = TestEntity.builder()
                .name(TEST_NAME)
                .description("4가지 핵심 지표를 통해 16가지 성격 유형을 알아보는 테스트입니다.")
                .testNum(2)
                .build();

        // ----------------------------------------------------------------------
        // 질문 목록 (40개) 정의 (이전 논의에서 작성된 내용)
        // ----------------------------------------------------------------------
        List<QuestionInfo> questionInfos = createQuestionInfos();

        int orderNo = 1;
        for (QuestionInfo info : questionInfos) {
            test.addQuestion(createQuestion(orderNo++, info.text, info.isReverseCoding));
        }

        // ===== 2. 결과 유형 추가 (16가지 유형) =====
        List<TestResultTypeEntity> results = createMbtiResults(test);
        results.forEach(test::addResultType);

        // ===== 3. 저장 =====
        testRepository.save(test);
        System.err.println(TEST_NAME + " 데이터 40문항과 16가지 결과 유형이 성공적으로 저장되었습니다.");
    }

    // 이하는 초기화에 필요한 헬퍼 메서드들입니다. (이전 논의 내용과 동일)

    private QuestionEntity createQuestion(int orderNo, String text, boolean isReverseCoding) {
        QuestionEntity question = QuestionEntity.builder()
                .orderNo(orderNo)
                .text(text)
                .build();

        // 점수가 높을수록 E, S, T, J 성향 (정방향 코딩)
        if (!isReverseCoding) {
            question.addChoice(ChoiceEntity.builder().orderNo(1).text(TEXT_VERY_MUCH).score(3).build());
            question.addChoice(ChoiceEntity.builder().orderNo(2).text(TEXT_SOMEWHAT).score(2).build());
            question.addChoice(ChoiceEntity.builder().orderNo(3).text(TEXT_NOT_MUCH).score(1).build());
            question.addChoice(ChoiceEntity.builder().orderNo(4).text(TEXT_NOT_AT_ALL).score(0).build());
        } else {
            // 역방향 코딩 (사용하지 않음)
            question.addChoice(ChoiceEntity.builder().orderNo(1).text(TEXT_VERY_MUCH).score(0).build());
            question.addChoice(ChoiceEntity.builder().orderNo(2).text(TEXT_SOMEWHAT).score(1).build());
            question.addChoice(ChoiceEntity.builder().orderNo(3).text(TEXT_NOT_MUCH).score(2).build());
            question.addChoice(ChoiceEntity.builder().orderNo(4).text(TEXT_NOT_AT_ALL).score(3).build());
        }
        return question;
    }

    // MbtiTestDataInitializer.java 파일의 createMbtiResults 메서드 수정

    private List<TestResultTypeEntity> createMbtiResults(TestEntity test) {
        List<TestResultTypeEntity> results = new ArrayList<>();

        // 이 메서드는 각 MBTI 유형(INTJ, ENFP 등)에 대한 구체적인 설명을 반환합니다.
        Map<String, String> mbtiDescriptions = getMbtiDescriptions();

        String[] types = {"E", "I", "S", "N", "T", "F", "J", "P"};

        // 16가지 유형 조합
        for (String ei : List.of(types[0], types[1])) {
            for (String sn : List.of(types[2], types[3])) {
                for (String tf : List.of(types[4], types[5])) {
                    for (String jp : List.of(types[6], types[7])) {
                        String mbtiType = ei + sn + tf + jp;

                        String description = mbtiDescriptions.getOrDefault(
                                mbtiType,
                                mbtiType + " 유형에 대한 기본 설명입니다. 데이터 누락 확인 필요."
                        );

                        results.add(TestResultTypeEntity.builder()
                                .test(test)
                                .minScore(0)
                                .maxScore(MAX_SCORE_PER_AXIS * 4) // 총점은 40문항 * 3점 = 120점
                                .resultName(mbtiType)
                                .description(description)
                                .build());
                    }
                }
            }
        }
        return results;
    }

    // 💡 MBTI 유형별 설명을 반환하는 헬퍼 메서드 추가
    private Map<String, String> getMbtiDescriptions() {
        Map<String, String> descriptions = new HashMap<>();

        // 분석가형 (NT)
        descriptions.put("INTJ", "\"용의주도한 전략가\"\n\n상상력이 풍부하며 통찰력이 뛰어난 논리적인 완벽주의자.");
        descriptions.put("INTP", "\"논리적인 사색가\"\n\n지칠 줄 모르는 지적 호기심을 가진 혁신적인 발명가.");
        descriptions.put("ENTJ", "\"대담한 통솔자\"\n\n대담하면서도 단호한 성격으로, 강력한 리더십을 발휘하는 지도자.");
        descriptions.put("ENTP", "\"뜨거운 논쟁을즐기는 변론가\"\n\n지적인 도전을 즐기며 언제나 한 수 앞서 생각하는 재기발랄한 사상가.");

        // 외교관형 (NF)
        descriptions.put("INFJ", "\"선의의 옹호자\"\n\n차분하고 신비로운 분위기로, 사람들에게 영감을 불어넣는 이타주의자.");
        descriptions.put("INFP", "\"열정적인 중재자\"\n\n상냥하고 이타적인 성격으로, 늘 더 나은 세상을 만드는 데 집중하는 몽상가.");
        descriptions.put("ENFJ", "\"정의로운 사회운동가\"\n\n넘치는 카리스마와 열정으로 청중을 압도하는 탁월한 선동가.");
        descriptions.put("ENFP", "\"자유로운 영혼의 활동가\"\n\n창의적이며 항상 웃을 거리를 찾아다니는 활발한 낙천주의자.");

        // 관리자형 (SJ)
        descriptions.put("ISTJ", "\"청렴결백한 논리주의자\"\n\n사실에 입각하여 사고하며, 논리적이고 현실적인 책임감이 강한 사람.");
        descriptions.put("ISFJ", "\"용감한 수호자\"\n\n성실하고 온정적인 성격으로, 타인을 보호할 책임감을 느낍니다.");
        descriptions.put("ESTJ", "\"엄격한 관리자\"\n\n철저한 관리와 체계적인 운영으로 세상을 이끄는 현실주의자.");
        descriptions.put("ESFJ", "\"사교적인 외교관\"\n\n타인을 향한 진심 어린 관심과 사교성으로 대중을 이끄는 화합주의자.");

        // 탐험가형 (SP)
        descriptions.put("ISTP", "\"만능 재주꾼\"\n\n대담하고 현실적인 성격으로, 다양한 도구 사용에 능숙한 장인.");
        descriptions.put("ISFP", "\"호기심 많은 예술가\"\n\n항상 새로운 것을 탐험하며, 아름다움을 발견하는 예술적 감각이 뛰어난 사람.");
        descriptions.put("ESTP", "\"모험을 즐기는 사업가\"\n\n리스크를 감수하며, 에너지 넘치고 재치 있는 행동가.");
        descriptions.put("ESFP", "\"자유로운 연예인\"\n\n즉흥적이고 넘치는 에너지로 주변 사람들을 즐겁게 만드는 즉흥주의자.");

        return descriptions;
    }

// 💡 주의: 이 메서드를 사용하려면 MbtiTestDataInitializer 클래스 상단에 HashMap 임포트가 필요합니다.
// import java.util.HashMap;
// import java.util.Map;

// ... (나머지 createQuestion, QuestionInfo 코드는 그대로 유지)

    private List<QuestionInfo> createQuestionInfos() {
        List<QuestionInfo> list = new ArrayList<>();
        // E/I 축 (1~10)
        list.add(new QuestionInfo("다수의 사람들과 함께 에너지를 얻는 편이다.", false));
        list.add(new QuestionInfo("혼자 있는 시간보다 사람들과 교류하는 시간을 선호한다.", false));
        list.add(new QuestionInfo("새로운 환경에서 쉽게 적응하고 먼저 말을 거는 편이다.", false));
        list.add(new QuestionInfo("긴 대화나 미팅 후에도 피로함을 느끼지 않는다.", false));
        list.add(new QuestionInfo("주변 사람들에게 나의 생각을 적극적으로 표현하는 편이다.", false));
        list.add(new QuestionInfo("계획에 없던 즉흥적인 모임에 쉽게 참여한다.", false));
        list.add(new QuestionInfo("복잡한 문제를 생각할 때, 다른 사람과 대화하면서 아이디어를 얻는다.", false));
        list.add(new QuestionInfo("관심받는 상황이나 사람들 앞에 나서는 것을 즐긴다.", false));
        list.add(new QuestionInfo("전화 통화가 문자 메시지보다 편하다.", false));
        list.add(new QuestionInfo("주말에는 집에 있기보다 외부 활동을 하는 것을 선호한다.", false));

        // S/N 축 (11~20)
        list.add(new QuestionInfo("나는 추상적인 이론보다 구체적이고 실용적인 사실에 관심이 많다.", false));
        list.add(new QuestionInfo("기억력이 좋아서 과거의 세부적인 일들을 잘 떠올리는 편이다.", false));
        list.add(new QuestionInfo("일을 할 때 직관이나 가능성보다 현재의 경험과 데이터를 중시한다.", false));
        list.add(new QuestionInfo("이야기할 때 비유나 은유를 사용하는 것보다 직접적인 설명을 선호한다.", false));
        list.add(new QuestionInfo("상상이나 공상에 많은 시간을 보내는 것을 비효율적이라고 생각한다.", false));
        list.add(new QuestionInfo("발생할 수 있는 최악의 상황을 구체적으로 계획하는 편이다.", false));
        list.add(new QuestionInfo("새로운 아이디어를 제안할 때, 현실적인 적용 가능성부터 따져본다.", false));
        list.add(new QuestionInfo("나는 눈앞에 보이는 현실에 집중할 때 가장 안정감을 느낀다.", false));
        list.add(new QuestionInfo("일의 과정보다 결과와 완성도를 중시한다.", false));
        list.add(new QuestionInfo("새로운 것을 배울 때, 단계별 학습과 실습을 선호한다.", false));

        // T/F 축 (21~30)
        list.add(new QuestionInfo("결정을 내릴 때, 개인적인 감정보다 객관적인 논리와 원칙을 따른다.", false));
        list.add(new QuestionInfo("나는 비판을 받았을 때 감정 상하기보다 그것이 합리적인지 분석한다.", false));
        list.add(new QuestionInfo("타인의 문제를 들을 때, 해결책을 제시하는 것이 공감해 주는 것보다 우선이다.", false));
        list.add(new QuestionInfo("공정함이란 모두에게 똑같은 기준을 적용하는 것이라고 생각한다.", false));
        list.add(new QuestionInfo("논쟁에서 감정적인 부분이 개입되는 것을 불편해 한다.", false));
        list.add(new QuestionInfo("상대방에게 조언할 때, 돌려 말하기보다 솔직하고 직접적으로 이야기한다.", false));
        list.add(new QuestionInfo("선택의 기로에서 효율성과 타당성을 가장 중요하게 생각한다.", false));
        list.add(new QuestionInfo("업무를 수행할 때 사람들의 감정을 고려하는 것은 시간 낭비일 수 있다.", false));
        list.add(new QuestionInfo("사람들에게 관심 두는 것보다 사물이나 기술에 대한 관심이 더 크다.", false));
        list.add(new QuestionInfo("감정에 쉽게 휘둘리지 않으며, 이성적으로 상황을 판단하는 편이다.", false));

        // J/P 축 (31~40)
        list.add(new QuestionInfo("나는 계획을 세우고 그것을 지키는 데서 안정감을 얻는다.", false));
        list.add(new QuestionInfo("중요한 마감 기한이 다가오면 미리 준비하고 완수해야 직성이 풀린다.", false));
        list.add(new QuestionInfo("즉흥적인 변화나 예상치 못한 상황을 선호하지 않으며 스트레스를 받는다.", false));
        list.add(new QuestionInfo("여행이나 휴가를 떠나기 전에 모든 일정을 상세하게 짜 놓는 편이다.", false));
        list.add(new QuestionInfo("결정을 내릴 때까지 시간을 끌기보다, 신속하게 판단하고 마무리 짓는 것을 좋아한다.", false));
        list.add(new QuestionInfo("집이나 주변 환경이 정돈되어 있지 않으면 집중하기 어렵다.", false));
        list.add(new QuestionInfo("일을 시작하기 전에 목표와 최종 결과물을 명확하게 설정해야 한다.", false));
        list.add(new QuestionInfo("놀이와 업무의 경계를 명확히 구분하고 싶어한다.", false));
        list.add(new QuestionInfo("나는 옵션을 열어두기보다, 빨리 결론을 내고 다음 단계로 나아가고 싶어 한다.", false));
        list.add(new QuestionInfo("어떤 일을 맡았을 때, 완료될 때까지 마음이 불편하다.", false));
        return list;
    }

    private static class QuestionInfo {
        String text;
        boolean isReverseCoding;
        public QuestionInfo(String text, boolean isReverseCoding) {
            this.text = text;
            this.isReverseCoding = isReverseCoding;
        }
    }
}