package com.couchcoding.oola.dto.study.response;

import com.couchcoding.oola.entity.Study;
import com.couchcoding.oola.entity.StudyMember;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class StudyResponseDetailDto {

    private Long studyId;

    private String studyType;

    private String studyName;

    private String studyDays;

    private String timeZone;

    private int participants;

    private int currentParticipants;

    private LocalDateTime startDate;

    private String openChatUrl;

    private String studyIntroduce;

    private String studyGoal;

    private String status;

    private String joinStatus;

    private LocalDateTime endDate;

    private boolean likeStatus;

    @Builder
    public StudyResponseDetailDto(Long studyId, String studyType, String studyName, String studyDays, String timeZone, int participants, int currentParticipants, LocalDateTime startDate, String openChatUrl, String studyIntroduce, String studyGoal, String status, String joinStatus, LocalDateTime endDate, boolean likeStatus) {
        this.studyId = studyId;
        this.studyType = studyType;
        this.studyName = studyName;
        this.studyDays = studyDays;
        this.timeZone = timeZone;
        this.participants = participants;
        this.currentParticipants = currentParticipants;
        this.startDate = startDate;
        this.openChatUrl = openChatUrl;
        this.studyIntroduce = studyIntroduce;
        this.studyGoal = studyGoal;
        this.status = status;
        this.joinStatus = joinStatus;
        this.endDate = endDate;
        this.likeStatus = likeStatus;
    }

    public StudyResponseDetailDto toDto(Study study) {
        StudyResponseDetailDto studyResponseDetailDto = StudyResponseDetailDto.builder()
                .studyId(study.getStudyId())
                .studyType(study.getStudyType())
                .studyDays(study.getStudyDays())
                .timeZone(study.getTimeZone())
                .participants(study.getParticipants())
                .currentParticipants(study.getCurrentParticipants())
                .startDate(study.getStartDate())
                .openChatUrl(study.getOpenChatUrl())
                .studyIntroduce(study.getStudyIntroduce())
                .studyGoal(study.getStudyGoal())
                .status(study.getStatus())
                .joinStatus(study.getJoinStatus())
                .likeStatus(true)
                .studyName(study.getStudyName())
                .endDate(study.getEndDate())
                .build();
        return studyResponseDetailDto;
    }
}