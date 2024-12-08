package com.photex.tiktok.models.restmodels;

/**
 * Created by Aurang Zeb on 13-Dec-16.
 */

public class UserProfessionalSkills {
    String userId;
    private String professionalSkills;
    private boolean isProfessionalSkillsPrivate;

    public String getProfessionalSkills() {
        return professionalSkills;
    }

    public void setProfessionalSkills(String professionalSkills) {
        this.professionalSkills = professionalSkills;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isProfessionalSkillsPrivate() {
        return isProfessionalSkillsPrivate;
    }

    public void setProfessionalSkillsPrivate(boolean professionalSkillsPrivate) {
        isProfessionalSkillsPrivate = professionalSkillsPrivate;
    }
}
