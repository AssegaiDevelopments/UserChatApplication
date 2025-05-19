import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.UIDefaults;

public class ModifiedFlatMacDarkLaf extends FlatMacDarkLaf {
    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void uninitialize() {
        super.uninitialize();
    }

    @Override
    public UIDefaults getDefaults() {
        UIDefaults defaults = super.getDefaults();
        defaults.put("TextField.arc", 15);
        defaults.put("Button.arc", 15);
        return defaults;
    }

    public static boolean setup() {
        return new ModifiedFlatMacDarkLaf().install();
    }
}