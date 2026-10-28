package com.dre4m1nd.cipherlens.common.util;

import cn.hutool.core.util.StrUtil;
import com.dre4m1nd.cipherlens.model.entity.PasswordEvaluationLog;
import com.dre4m1nd.cipherlens.service.WeakPasswordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author dre4m1nd
 */
@Component
public class SecurityEvaluationUtil {

    @Resource
    private WeakPasswordService weakPasswordService;

    // 正则规则
    private static final Pattern PATTERN_NUMBER = Pattern.compile("\\d");
    private static final Pattern PATTERN_UPPER_CASE = Pattern.compile("[A-Z]");
    private static final Pattern PATTERN_LOWER_CASE = Pattern.compile("[a-z]");
    private static final Pattern PATTERN_SPECIAL_CHAR = Pattern.compile("[^a-zA-Z0-9]");

    // 密码长度阈值
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int PWD_LENGTH_MID = 12;
    private static final int PWD_LENGTH_MAX = 16;

    // 字符类型阈值
    private static final int MIN_CHAR_TYPE_COUNT = 2;
    private static final int CHAR_TYPE_THREE = 3;
    private static final int CHAR_TYPE_FOUR = 4;

    // 连续/
    private static final int MAX_CONTINUOUS_CHAR = 3;
    private static final int MAX_CONTINUOUS_REPEAT_CHAR = 4;
    private static final int MAX_GLOBAL_REPEAT_RATIO = 50;

    // 风险提示
    public static final String RISK_TYPE_IS_BLANK = "密码为空";
    public static final String RISK_TYPE_WEAK_PASSWORD = "密码为弱密码";
    public static final String RISK_TYPE_LESS_MIN_LENGTH = String.format("密码长度小于%d", MIN_PASSWORD_LENGTH);
    public static final String RISK_TYPE_LESS_MIN_TYPE_COUNT = String.format("密码字符类型少于%d种", MIN_CHAR_TYPE_COUNT);
    public static final String RISK_TYPE_CONSECUTIVE_CHAR = String.format("密码包含连续字符（数字/字母），连续长度不能超过%d个", MAX_CONTINUOUS_CHAR - 1);
    public static final String RISK_TYPE_REPEAT_CHAR = String.format("密码包含重复字符（连续重复超%d个或全局重复占比超%d%%）", MAX_CONTINUOUS_REPEAT_CHAR, MAX_GLOBAL_REPEAT_RATIO);


    private static final String SUGGESTION_IS_BLANK = "请设置非空密码，建议结合字母、数字和特殊符号组合";
    private static final String SUGGESTION_WEAK_PASSWORD = "请更换高强度密码，避免使用常见弱口令（如123456、password等）";
    private static final String SUGGESTION_LESS_MIN_LENGTH = String.format("请将密码长度提升至%d位以上，推荐16位以获得更高安全性", MIN_PASSWORD_LENGTH);
    private static final String SUGGESTION_LESS_MIN_TYPE_COUNT = "请至少包含2类及以上字符（数字、大写字母、小写字母、特殊符号），推荐4类全覆盖";
    private static final String SUGGESTION_CONSECUTIVE_CHAR = String.format("请移除密码中的连续字符（如123、abc、CBA等），连续个数不超过%d个", MAX_CONTINUOUS_CHAR - 1);
    private static final String SUGGESTION_REPEAT_CHAR = String.format("请减少密码中重复字符的使用：1.单个字符连续重复不超过%d次 2.全局重复字符占比不超过%d%%", MAX_CONTINUOUS_REPEAT_CHAR, MAX_GLOBAL_REPEAT_RATIO);
    private static final String SUGGESTION_FULL_SCORE = "密码安全性已达满分，建议定期更换（如90天/次），并避免在多个平台复用";
    private static final String SUGGESTION_DEFAULT = "请根据以下建议优化密码：1.长度≥16位 2.包含数字、大小写字母、特殊符号 3.无连续/重复字符 4.非弱口令";

    /**
     * 密码评估
     * @param userId 用户ID
     * @param passwordItemId 密码项ID
     * @param password 密码
     * @return 密码评估结果（包含suggestions字段）
     */
    public PasswordEvaluationLog evaluate(Long userId, Long passwordItemId, String password) {
        PasswordEvaluationLog log = new PasswordEvaluationLog();
        log.setUserId(userId);
        log.setPasswordItemId(passwordItemId);
        log.setEvaluateTime(LocalDateTime.now());

        // 获取风险项
        List<String> riskTypes = getRiskTypes(password);
        // 计算安全分
        int score = calculateSecurityScore(password, riskTypes);
        // 生成优化建议
        String suggestions = generateOptimizationSuggestions(password, riskTypes, score);

        log.setEvaluateScore(score);
        log.setRiskType(String.join(",", riskTypes));
        // 设置suggestions字段
        log.setSuggestions(suggestions);

        return log;
    }

    /**
     * 获取风险项
     * @param password 密码
     * @return 风险项
     */
    private List<String> getRiskTypes(String password) {
        List<String> riskTypes = new ArrayList<>();
        // 1.非空校验
        if (StrUtil.isBlank(password)) {
            riskTypes.add(RISK_TYPE_IS_BLANK);
            // 空密码直接返回，无需后续校验
            return riskTypes;
        }
        // 2.弱密码校验
        if (weakPasswordService.isWeakPassword(password)) {
            riskTypes.add(RISK_TYPE_WEAK_PASSWORD);
        }
        // 3.密码长度校验
        if (password.length() < MIN_PASSWORD_LENGTH) {
            riskTypes.add(RISK_TYPE_LESS_MIN_LENGTH);
        }
        // 4.字符类型校验
        if (getCharTypeCount(password) < MIN_CHAR_TYPE_COUNT) {
            riskTypes.add(RISK_TYPE_LESS_MIN_TYPE_COUNT);
        }
        // 5.连续字符校验
        if (hasContinuousChars(password)) {
            riskTypes.add(RISK_TYPE_CONSECUTIVE_CHAR);
        }
        // 6.重复字符校验
        if (hasExcessiveRepeatChars(password)) {
            riskTypes.add(RISK_TYPE_REPEAT_CHAR);
        }
        return riskTypes;
    }

    /**
     * 生成密码优化建议
     * @param password 密码
     * @param riskTypes 风险项
     * @param score 安全分
     * @return 针对性的优化建议
     */
    private String generateOptimizationSuggestions(String password, List<String> riskTypes, int score) {
        // 满分情况
        if (score == 100) {
            return SUGGESTION_FULL_SCORE;
        }

        // 空密码
        if (riskTypes.contains(RISK_TYPE_IS_BLANK)) {
            return SUGGESTION_IS_BLANK;
        }

        // 拼接针对性建议
        List<String> suggestionList = new ArrayList<>();
        if (riskTypes.contains(RISK_TYPE_WEAK_PASSWORD)) {
            suggestionList.add(SUGGESTION_WEAK_PASSWORD);
        }
        if (riskTypes.contains(RISK_TYPE_LESS_MIN_LENGTH)) {
            suggestionList.add(SUGGESTION_LESS_MIN_LENGTH);
        }
        if (riskTypes.contains(RISK_TYPE_LESS_MIN_TYPE_COUNT)) {
            suggestionList.add(SUGGESTION_LESS_MIN_TYPE_COUNT);
        }
        if (riskTypes.contains(RISK_TYPE_CONSECUTIVE_CHAR)) {
            suggestionList.add(SUGGESTION_CONSECUTIVE_CHAR);
        }
        if (riskTypes.contains(RISK_TYPE_REPEAT_CHAR)) {
            suggestionList.add(SUGGESTION_REPEAT_CHAR);
        }

        // 有具体风险项则拼接，否则返回默认建议
        if (!suggestionList.isEmpty()) {
            return String.join("；", suggestionList);
        } else {
            // 非满分但无明显风险项的情况（如长度12位可优化至16位）
            StringBuilder sb = new StringBuilder();
            if (StrUtil.isNotBlank(password)) {
                if (password.length() >= MIN_PASSWORD_LENGTH && password.length() < PWD_LENGTH_MAX) {
                    sb.append(String.format("当前密码长度为%d位，建议提升至16位以获得更高安全性；", password.length()));
                }
                int charTypeCount = getCharTypeCount(password);
                if (charTypeCount < CHAR_TYPE_FOUR) {
                    sb.append(String.format("当前密码包含%d类字符，建议补充至4类（数字、大小写字母、特殊符号）以提升安全性；", charTypeCount));
                }
                // 移除最后一个分号
                if (!sb.isEmpty()) {
                    sb.setLength(sb.length() - 1);
                    return sb.toString();
                }
            }
            return SUGGESTION_DEFAULT;
        }
    }

    /**
     * 计算安全分（新策略）
     * @param password 密码
     * @param riskTypes 风险项
     * @return 0-100分的安全分
     */
    private Integer calculateSecurityScore(String password, List<String> riskTypes) {
        // 1.空密码直接得0分
        if (riskTypes.contains(RISK_TYPE_IS_BLANK)) {
            return 0;
        }

        // 基础分
        int totalScore = 30;

        // 2.长度加分
        int pwdLength = password.length();
        if (pwdLength >= MIN_PASSWORD_LENGTH && pwdLength < PWD_LENGTH_MID) {
            totalScore += 10;
        } else if (pwdLength >= PWD_LENGTH_MID && pwdLength < PWD_LENGTH_MAX) {
            totalScore += 20;
        } else if (pwdLength >= PWD_LENGTH_MAX) {
            totalScore += 30;
        }

        // 3.字符类型加分
        int charTypeCount = getCharTypeCount(password);
        if (charTypeCount == MIN_CHAR_TYPE_COUNT) {
            totalScore += 10;
        } else if (charTypeCount == CHAR_TYPE_THREE) {
            totalScore += 20;
        } else if (charTypeCount == CHAR_TYPE_FOUR) {
            totalScore += 30;
        }

        // 4.风险项扣分
        if (riskTypes.contains(RISK_TYPE_WEAK_PASSWORD)) {
            totalScore -= 40;
        }
        if (riskTypes.contains(RISK_TYPE_LESS_MIN_LENGTH)) {
            totalScore -= 30;
        }
        if (riskTypes.contains(RISK_TYPE_CONSECUTIVE_CHAR)) {
            totalScore -= 20;
        }
        if (riskTypes.contains(RISK_TYPE_REPEAT_CHAR)) {
            totalScore -= 20;
        }

        // 5.兜底规则：分数限制在0-100之间
        return Math.max(0, Math.min(100, totalScore));
    }

    /**
     * 获取字符类型数量
     * @param password 密码
     * @return 字符类型数量（数字/大写/小写/特殊字符）
     */
    private int getCharTypeCount(String password) {
        int count = 0;
        if (PATTERN_NUMBER.matcher(password).find()) {
            count++;
        }
        if (PATTERN_UPPER_CASE.matcher(password).find()) {
            count++;
        }
        if (PATTERN_LOWER_CASE.matcher(password).find()) {
            count++;
        }
        if (PATTERN_SPECIAL_CHAR.matcher(password).find()) {
            count++;
        }
        return count;
    }

    /**
     * 重写：判断密码中是否包含连续字符（数字/字母，支持大小写不敏感）
     * 规则：
     * 1. 仅检测数字和字母的连续（排除特殊字符）
     * 2. 正向连续（123、abc）或反向连续（321、cba）均判定为连续
     * 3. 字母连续忽略大小写（AbC视为abc连续）
     * 4. 连续长度≥MAX_CONTINUOUS_CHAR时判定为风险
     * @param password 密码
     * @return 是否包含违规连续字符
     */
    private boolean hasContinuousChars(String password) {
        // 密码长度不足直接返回false
        if (StrUtil.isBlank(password) || password.length() < MAX_CONTINUOUS_CHAR) {
            return false;
        }

        char[] chars = password.toCharArray();
        // 遍历检查每个起始位置的连续字符
        for (int i = 0; i <= chars.length - MAX_CONTINUOUS_CHAR; i++) {
            // 提取当前窗口的字符
            char[] window = new char[MAX_CONTINUOUS_CHAR];
            System.arraycopy(chars, i, window, 0, MAX_CONTINUOUS_CHAR);

            // 检查是否为纯数字或纯字母（排除特殊字符混合）
            if (isAllDigit(window) || isAllLetter(window)) {
                // 转换为小写（字母统一处理）
                char[] lowerWindow = new char[MAX_CONTINUOUS_CHAR];
                for (int j = 0; j < MAX_CONTINUOUS_CHAR; j++) {
                    lowerWindow[j] = Character.toLowerCase(window[j]);
                }

                // 检查正向连续
                boolean isForwardContinuous = true;
                // 检查反向连续
                boolean isBackwardContinuous = true;

                for (int j = 1; j < MAX_CONTINUOUS_CHAR; j++) {
                    // 正向：后一个字符 = 前一个 +1
                    isForwardContinuous &= (lowerWindow[j] == lowerWindow[j-1] + 1);
                    // 反向：后一个字符 = 前一个 -1
                    isBackwardContinuous &= (lowerWindow[j] == lowerWindow[j-1] - 1);

                    // 两者都不满足，提前终止
                    if (!isForwardContinuous && !isBackwardContinuous) {
                        break;
                    }
                }

                if (isForwardContinuous || isBackwardContinuous) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 重写：判断密码中是否包含过度重复字符
     * 规则：
     * 1. 连续重复：单个字符连续出现超过MAX_CONTINUOUS_REPEAT_CHAR次
     * 2. 全局重复：单个字符出现次数占比超过MAX_GLOBAL_REPEAT_RATIO%
     * 满足任一条件即判定为风险
     * @param password 密码
     * @return 是否包含违规重复字符
     */
    private boolean hasExcessiveRepeatChars(String password) {
        if (StrUtil.isBlank(password)) {
            return false;
        }

        char[] chars = password.toCharArray();
        int totalLength = chars.length;

        // 1. 检查连续重复字符
        int currentRepeatCount = 1;
        char prevChar = chars[0];
        for (int i = 1; i < totalLength; i++) {
            if (chars[i] == prevChar) {
                currentRepeatCount++;
                // 连续重复超过阈值，直接返回true
                if (currentRepeatCount > MAX_CONTINUOUS_REPEAT_CHAR) {
                    return true;
                }
            } else {
                // 重置计数
                currentRepeatCount = 1;
                prevChar = chars[i];
            }
        }

        // 2. 检查全局重复占比
        Map<Character, Integer> charCountMap = new HashMap<>();
        for (char c : chars) {
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }

        // 计算每个字符的占比
        for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
            int count = entry.getValue();
            int ratio = (count * 100) / totalLength;
            if (ratio > MAX_GLOBAL_REPEAT_RATIO) {
                return true;
            }
        }

        return false;
    }

    /**
     * 辅助方法：判断字符数组是否全为数字
     */
    private boolean isAllDigit(char[] chars) {
        for (char c : chars) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 辅助方法：判断字符数组是否全为字母
     */
    private boolean isAllLetter(char[] chars) {
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
}