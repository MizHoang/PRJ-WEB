package models;

import java.util.Date;

public class Submission {
    private int submissionId;
    private int assignmentId;
    private int studentId;
    private String filePath;
    private Date submitDate;
    private String status;

    public Submission() {}

    // Getters & Setters (bạn có thể generate)
    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }
    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Date getSubmitDate() { return submitDate; }
    public void setSubmitDate(Date submitDate) { this.submitDate = submitDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}