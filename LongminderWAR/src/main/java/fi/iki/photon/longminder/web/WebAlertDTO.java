package fi.iki.photon.longminder.web;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import fi.iki.photon.longminder.entity.dto.AlertDTO;

public class WebAlertDTO extends AlertDTO {
    
    public boolean validate() {
        if ("".equals(getDescription())) {
            final FacesMessage msg = new FacesMessage(
                    "Error creating the alert: Set the description.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }

        if (getRepeatType() == AlertDTO.REPEAT_DAY && getDayDelay() < 1) {
            final FacesMessage msg = new FacesMessage(
                    "Error creating the alert: Day delay value must be a positive integer.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }

        if (getRepeatType() == AlertDTO.REPEAT_WEEK && getWeekDelay() < 1) {
            final FacesMessage msg = new FacesMessage(
                    "Error creating the alert: Week delay value must be a positive integer.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return false;
        }

        if (getRepeatType() == AlertDTO.REPEAT_MONTH) {
            if (getMonthDelay() < 1) {
                final FacesMessage msg = new FacesMessage(
                        "Error creating the alert: Month delay value must be a positive integer.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
            if (getAlertMonthDay() < 1 || getAlertMonthDay() > 31) {
                final FacesMessage msg = new FacesMessage(
                        "Error creating the alert: Month day value must be between 1 and 31.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
        }

        if (getRepeatType() == AlertDTO.REPEAT_YEAR) {
            if (getYearDelay() < 1) {
                final FacesMessage msg = new FacesMessage(
                        "Error creating the alert: Year delay value must be a positive integer.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
            if (getAlertMonthDay() < 1 || getAlertMonthDay() > 366) {
                final FacesMessage msg = new FacesMessage(
                        "Error creating the alert: Year day value must be between 1 and 366.");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return false;
            }
        }


        return true;
    }
}
