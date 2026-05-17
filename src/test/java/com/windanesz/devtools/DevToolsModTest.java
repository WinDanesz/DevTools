package com.windanesz.devtools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DevToolsModTest {

    @Test
    void sanitizeWorldSaveNameReplacesIllegalCharacters() {
        assertEquals("DevTools_Test_World_", DevToolsMod.sanitizeWorldSaveName("DevTools/Test.World\""));
    }

    @Test
    void sanitizeWorldSaveNameProtectsReservedWindowsNames() {
        assertEquals("_CON_", DevToolsMod.sanitizeWorldSaveName("CON"));
    }

    @Test
    void sanitizeWorldSaveNameFallsBackToWorldForBlankInput() {
        assertEquals("World", DevToolsMod.sanitizeWorldSaveName("   "));
    }
}
