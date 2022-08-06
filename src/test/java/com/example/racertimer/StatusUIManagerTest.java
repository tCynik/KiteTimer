package com.example.racertimer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StatusUIManagerTest {
    boolean result;

    StatusUiUpdater statusUiUpdater = new StatusUiUpdater() {
        @Override
        public void onStatusChecked(boolean status) {
            result = status;
        }

        @Override
        public void updateUIModuleStatus(String moduleName) {
            statusUIManager.setModuleStatus(moduleName);
        }
    };

    StatusUIManager statusUIManager = new StatusUIManager(new String[]{"one, two, three"}, statusUiUpdater);


    @Test
    public void checkNoMatchModuleNames() throws Exception {
        statusUiUpdater.updateUIModuleStatus("bla");
        statusUiUpdater.updateUIModuleStatus("bla");
        statusUiUpdater.updateUIModuleStatus("bla");
        assertEquals(false, result);
    }

    @Test
    public void checkPartlyMatchModuleNames() throws Exception {
        statusUiUpdater.updateUIModuleStatus("one");
        statusUiUpdater.updateUIModuleStatus("two");
        statusUiUpdater.updateUIModuleStatus("bla");
        assertEquals(false, result);
    }

    @Test
    public void checkFullMatchModuleNames() throws Exception {
        statusUiUpdater.updateUIModuleStatus("one");
        statusUiUpdater.updateUIModuleStatus("two");
        statusUiUpdater.updateUIModuleStatus("three");
        assertEquals(true, result);
    }

    @Test
    public void checkMixedFullMatchModuleNames() throws Exception {
        statusUiUpdater.updateUIModuleStatus("three");
        statusUiUpdater.updateUIModuleStatus("two");
        statusUiUpdater.updateUIModuleStatus("two");
        statusUiUpdater.updateUIModuleStatus("bla");
        statusUiUpdater.updateUIModuleStatus("one");
        assertEquals(true, result);
    }


}
