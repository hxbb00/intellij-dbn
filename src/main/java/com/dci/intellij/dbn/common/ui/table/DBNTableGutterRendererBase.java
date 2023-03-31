package com.dci.intellij.dbn.common.ui.table;

import com.dci.intellij.dbn.common.color.Colors;
import com.dci.intellij.dbn.common.dispose.Failsafe;
import com.dci.intellij.dbn.common.latent.Latent;
import com.dci.intellij.dbn.common.thread.Dispatch;
import com.dci.intellij.dbn.common.ui.util.Borders;
import com.dci.intellij.dbn.common.ui.util.Fonts;
import com.intellij.ui.border.CustomLineBorder;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.util.HashMap;
import java.util.Map;

public abstract class DBNTableGutterRendererBase implements DBNTableGutterRenderer{
    protected JLabel textLabel;
    protected JLabel iconLabel;
    protected JPanel mainPanel;

    private final Latent<Map<Integer, Integer>> indexWidth = Latent.mutable(
            () -> textLabel.getFont(),
            () -> new HashMap<>());

    public DBNTableGutterRendererBase() {
        textLabel.setText("");
        iconLabel.setText("");
        textLabel.setFont(Fonts.getEditorFont());
        textLabel.setForeground(Colors.getTableGutterForeground());
        mainPanel.setBackground(Colors.getTableGutterBackground());
        mainPanel.setPreferredSize(new Dimension(40, -1));
        iconLabel.setBorder(Borders.insetBorder(4));


        Border border = new CustomLineBorder(Colors.getTableHeaderGridColor(), 0, 0, 0, 1);
        mainPanel.setBorder(border);
    }

    @Override
    public final Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        adjustListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        textLabel.setText(Integer.toString(index + 1));
        int textWidth = computeLabelWidth(list.getModel().getSize());
        int iconWidth = iconLabel.getIcon() == null ? 0 : 16;
        //iconLabel.setVisible(iconLabel.getIcon() == null);

        int preferredWidth = textWidth + iconWidth + 16;

        Dimension preferredSize = mainPanel.getPreferredSize();
        if (preferredSize.getWidth() != preferredWidth) {
            Dimension dimension = new Dimension(preferredWidth, -1);
            mainPanel.setPreferredSize(dimension);
            Dispatch.run(() -> resize(list, preferredWidth));
        }
        return mainPanel;
    }

    private void resize(JList list, int preferredWidth) {
        Failsafe.nd(list);
        list.setPreferredSize(new Dimension(preferredWidth, (int) list.getPreferredSize().getHeight()));
    }

    private int computeLabelWidth(int count) {
        return indexWidth.get().computeIfAbsent(count, c -> {
            int digits = (int) Math.log10(c) + 1;
            String text = StringUtils.leftPad("", digits, "0");
            Font font = textLabel.getFont();
            FontRenderContext fontRenderContext = textLabel.getFontMetrics(font).getFontRenderContext();
            return (int) font.getStringBounds(text, fontRenderContext).getWidth();
        });
    }

    protected abstract void adjustListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus);
}
