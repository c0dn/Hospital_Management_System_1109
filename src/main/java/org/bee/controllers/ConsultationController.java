package org.bee.controllers;

import org.bee.hms.medical.Consultation;

public class ConsultationController extends BaseController<Consultation> {
    @Override
    protected String getDataFilePath() {
        return "";
    }

    @Override
    protected void generateInitialData() {

    }

    @Override
    protected Class<Consultation> getEntityClass() {
        return null;
    }
}
