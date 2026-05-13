package dcare;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DcareApp extends JFrame {

    // ── 색상 팔레트 ──────────────────────────────────────────────
    private static final Color BLUE_DARK   = hex("#1a5fa8");
    private static final Color BLUE_MID    = hex("#154d8c");
    private static final Color BLUE_LIGHT  = hex("#e6f1fb");
    private static final Color BLUE_TEXT   = hex("#0c447c");
    private static final Color GREEN_LIGHT = hex("#e1f5ee");
    private static final Color GREEN_TEXT  = hex("#0f6e56");
    private static final Color AMBER_LIGHT = hex("#faeeda");
    private static final Color AMBER_TEXT  = hex("#854f0b");
    private static final Color RED_LIGHT   = hex("#fdecea");
    private static final Color RED_TEXT    = hex("#a32d2d");
    private static final Color BG_WHITE    = hex("#ffffff");
    private static final Color BG_SURFACE  = hex("#f5f7fa");
    private static final Color BORDER_COL  = hex("#e2e6ea");
    private static final Color TEXT_PRI    = hex("#1a202c");
    private static final Color TEXT_SEC    = hex("#718096");

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private DcareService service;

    // 헤더 카운트 라벨 (실시간 갱신)
    private JLabel headerCountLabel;

    // 투여 탭 컴포넌트
    private JTextField weightField, timeField;
    private JRadioButton longBtn, shortBtn;
    private JLabel dosePreviewLabel, doseCalcLabel;
    private Dog selectedDog = null;

    // 탭 패널
    private JPanel injPanel, statsPanel, histPanel, managePanel;

    // ── 진입점 ────────────────────────────────────────────────────
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new DcareApp().setVisible(true));
    }

    public DcareApp() {
        service = new DcareService();

        setTitle("D-Care · 스마트 다견 관리");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 660);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);

        refreshAll();
    }

    // ── 헤더 ─────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE_DARK);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // 왼쪽: 아이콘 + 타이틀
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🐾");
        icon.setFont(new Font("Dialog", Font.PLAIN, 20));

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        JLabel t1 = new JLabel("D-Care");
        t1.setFont(new Font("Dialog", Font.BOLD, 15));
        t1.setForeground(Color.WHITE);
        JLabel t2 = new JLabel("스마트 다견 관리 시스템");
        t2.setFont(new Font("Dialog", Font.PLAIN, 11));
        t2.setForeground(new Color(255, 255, 255, 165));
        titles.add(t1); titles.add(t2);
        left.add(icon); left.add(titles);

        // 오른쪽: 마리 수 뱃지
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        badge.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Dialog", Font.PLAIN, 8));
        dot.setForeground(hex("#5dcaa5"));

        headerCountLabel = new JLabel(service.getDogList().size() + "마리 관리 중");
        headerCountLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        headerCountLabel.setForeground(new Color(255, 255, 255, 217));

        badge.add(dot); badge.add(headerCountLabel);

        header.add(left,  BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);
        return header;
    }

    // ── 탭 구성 ──────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Dialog", Font.PLAIN, 13));

        injPanel    = makeBoxPanel(); statsPanel = makeBoxPanel();
        histPanel   = makeBoxPanel(); managePanel = makeBoxPanel();

        tabbedPane.addTab("💉  투여",    scroll(injPanel));
        tabbedPane.addTab("📊  통계",    scroll(statsPanel));
        tabbedPane.addTab("📂  기록",    scroll(histPanel));
        tabbedPane.addTab("⚙️  반려견",  scroll(managePanel));

        tabbedPane.addChangeListener(e -> refreshPanel(tabbedPane.getSelectedIndex()));
        return tabbedPane;
    }

    private JPanel makeBoxPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_WHITE);
        return p;
    }

    private JScrollPane scroll(JPanel p) {
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }

    // ── 패널 라우터 ───────────────────────────────────────────────
    private void refreshPanel(int idx) {
        switch (idx) { case 0->buildInj(); case 1->buildStats(); case 2->buildHist(); case 3->buildManage(); }
    }

    private void refreshAll() {
        buildInj(); buildStats(); buildHist(); buildManage();
        if (headerCountLabel != null)
            headerCountLabel.setText(service.getDogList().size() + "마리 관리 중");
    }

    // ══════════════════════════════════════════════════════════════
    // 탭 1 · 인슐린 투여
    // ══════════════════════════════════════════════════════════════
    private void buildInj() {
        injPanel.removeAll();
        injPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        List<Dog> dogs = service.getDogList();

        if (dogs.isEmpty()) {
            injPanel.add(emptyState("등록된 반려견이 없습니다", "먼저 [반려견] 탭에서 등록해 주세요."));
            refresh(injPanel); return;
        }
        if (selectedDog == null || !dogs.contains(selectedDog)) selectedDog = dogs.get(0);

        // 알림 바
        injPanel.add(fixWidth(alertBar("⏰  " + selectedDog.name + " — 투여 시간을 확인해 주세요")));
        injPanel.add(vgap(10));

        // 반려견 카드 선택
        injPanel.add(sectionLbl("반려견 선택"));
        injPanel.add(vgap(6));
        JPanel cardRow = new JPanel(new GridLayout(1, dogs.size(), 8, 0));
        cardRow.setOpaque(false);
        cardRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        for (Dog dog : dogs) cardRow.add(makeDogCard(dog));
        injPanel.add(cardRow);
        injPanel.add(vgap(10));
        injPanel.add(fixWidth(divider()));
        injPanel.add(vgap(10));

        // 입력 필드
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 4));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        weightField = styledField(String.valueOf(selectedDog.weight));
        timeField   = styledField(LocalTime.now().format(TIME_FMT));
        grid.add(inputLbl("현재 몸무게 (kg)")); grid.add(inputLbl("투여 시간 (HH:mm / now)"));
        grid.add(weightField); grid.add(timeField);
        injPanel.add(grid);
        injPanel.add(vgap(14));

        // 인슐린 종류
        injPanel.add(sectionLbl("인슐린 종류"));
        injPanel.add(vgap(6));
        ButtonGroup insulinGroup = new ButtonGroup();
        longBtn  = new JRadioButton("<html><b>💧 지속성 인슐린</b><br><small>캐닌슐린 · 12시간 간격 · 0.5 units/kg</small></html>", true);
        shortBtn = new JRadioButton("<html><b>⚡ 속효성 인슐린</b><br><small>휴멀린 · 6시간 간격 · 0.3 units/kg</small></html>", false);
        styleRadioBtn(longBtn); styleRadioBtn(shortBtn);
        insulinGroup.add(longBtn); insulinGroup.add(shortBtn);
        JPanel insulinRow = new JPanel(new GridLayout(1, 2, 10, 0));
        insulinRow.setOpaque(false);
        insulinRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        insulinRow.add(longBtn); insulinRow.add(shortBtn);
        injPanel.add(insulinRow);
        injPanel.add(vgap(10));

        // 투여량 미리보기
        JPanel preview = new JPanel();
        preview.setLayout(new BoxLayout(preview, BoxLayout.Y_AXIS));
        preview.setBackground(BLUE_LIGHT);
        preview.setBorder(new CompoundBorder(
            new LineBorder(hex("#b5d4f4"), 1, true),
            new EmptyBorder(12, 16, 12, 16)));
        preview.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        doseCalcLabel    = new JLabel("예상 투여량");
        doseCalcLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        doseCalcLabel.setForeground(hex("#185fa5"));
        doseCalcLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        dosePreviewLabel = new JLabel("— units");
        dosePreviewLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        dosePreviewLabel.setForeground(BLUE_TEXT);
        dosePreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        preview.add(doseCalcLabel); preview.add(dosePreviewLabel);
        injPanel.add(preview);
        injPanel.add(vgap(14));
        updateDosePreview();

        // 리스너
        weightField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updateDosePreview(); }
            public void removeUpdate(DocumentEvent e)  { updateDosePreview(); }
            public void changedUpdate(DocumentEvent e) { updateDosePreview(); }
        });
        longBtn.addActionListener(e -> updateDosePreview());
        shortBtn.addActionListener(e -> updateDosePreview());

        // 저장 버튼
        JButton saveBtn = new JButton("✅  투여 기록 저장");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        saveBtn.setBackground(BLUE_DARK);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Dialog", Font.BOLD, 14));
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> handleSave());
        injPanel.add(saveBtn);

        refresh(injPanel);
    }

    private JPanel makeDogCard(Dog dog) {
        boolean sel = (dog == selectedDog);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(sel ? BLUE_LIGHT : BG_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(sel ? BLUE_DARK : BORDER_COL, sel ? 2 : 1, true),
            new EmptyBorder(10, 12, 10, 12)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel name = new JLabel(dog.name);
        name.setFont(new Font("Dialog", Font.BOLD, 14));
        name.setForeground(sel ? BLUE_TEXT : TEXT_PRI);

        JLabel weight = new JLabel(dog.weight + " kg");
        weight.setFont(new Font("Dialog", Font.PLAIN, 12));
        weight.setForeground(TEXT_SEC);

        JLabel badge = badgeLbl("관리 중", GREEN_LIGHT, GREEN_TEXT);

        card.add(name); card.add(weight); card.add(Box.createVerticalStrut(4)); card.add(badge);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedDog = dog;
                if (weightField != null) weightField.setText(String.valueOf(dog.weight));
                buildInj();
            }
            public void mouseEntered(MouseEvent e) { if (dog != selectedDog) card.setBackground(hex("#f0f7ff")); }
            public void mouseExited(MouseEvent e)  { if (dog != selectedDog) card.setBackground(BG_WHITE); }
        });
        return card;
    }

    private void updateDosePreview() {
        if (dosePreviewLabel == null || weightField == null) return;
        try {
            double w = Double.parseDouble(weightField.getText().trim());
            boolean isLong = (longBtn != null && longBtn.isSelected());
            double factor = isLong ? 0.5 : 0.3;
            double maxDose = isLong ? 10.0 : 6.0;
            double dose = w * factor;
            String dogName = selectedDog != null ? selectedDog.name : "?";
            doseCalcLabel.setText("예상 투여량  —  " + dogName + " (" + w + "kg) × " + factor);
            if (dose > maxDose) {
                // 최대 용량 초과 → 빨간색 경고
                dosePreviewLabel.setText(String.format("%.1f units  ⚠️ 최대 %.1f 초과!", dose, maxDose));
                dosePreviewLabel.setForeground(RED_TEXT);
            } else {
                dosePreviewLabel.setText(String.format("%.1f units", dose));
                dosePreviewLabel.setForeground(BLUE_TEXT);
            }
        } catch (Exception ex) {
            doseCalcLabel.setText("예상 투여량");
            dosePreviewLabel.setText("— units");
            dosePreviewLabel.setForeground(BLUE_TEXT);
        }
    }

    private void handleSave() {
        if (selectedDog == null) { showAlert("반려견을 선택해 주세요."); return; }
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            String tStr = timeField.getText().trim();
            String time = tStr.equalsIgnoreCase("now") ? LocalTime.now().format(TIME_FMT) : tStr;
            boolean isLong = (longBtn != null && longBtn.isSelected());
            Insulin insulin = isLong ? new LongActingInsulin() : new ShortActingInsulin();

            // 먼저 용량만 계산해서 최대 용량 초과 여부를 미리 확인
            double previewDose = insulin.calculateDose(weight);
            if (previewDose > insulin.getMaxDose()) {
                // 최대 용량 초과 → 무조건 차단, 팝업만 표시
                showDangerAlert(String.format(
                    "⛔  위험: 계산 용량 %.1f units가 최대 허용량 %.1f units 초과!\n투여를 중단하고 수의사에게 문의하세요.",
                    previewDose, insulin.getMaxDose()));
                return; // 저장하지 않음
            }

            try {
                Dog actual = service.addInjection(selectedDog, insulin, weight, time);
                refreshAll();
                if (!actual.name.equals(selectedDog.name)) {
                    showInfo("⚠️ 몸무게가 달라 새 반려견으로 등록되었습니다.\n👉 [" + actual.name + "] (" + actual.weight + "kg) 으로 기록 완료!");
                    selectedDog = actual;
                } else {
                    showInfo("✅ " + actual.name + " 투여 기록이 완료되었습니다!");
                }
            } catch (MedicalDangerException mde) {
                // 간격 경고 → 사용자가 선택 (예/아니오)
                boolean proceed = showDangerConfirm(mde.getMessage());
                if (proceed) {
                    // 강행: 검증을 우회해 직접 기록만 저장
                    forceSave(selectedDog, insulin, weight, time);
                }
            }
        } catch (Exception e) { showAlert("⚠️ 입력 값을 다시 확인해 주세요."); }
    }

    /** 검증을 우회하고 강제 저장 (간격 경고 무시 시) */
    private void forceSave(Dog dog, Insulin insulin, double weight, String time) {
        try {
            double dose = insulin.calculateDose(weight);
            String record = String.format("[%s] %s: %.1f units (%s) ⚠️강행",
                            time, dog.name, dose, insulin.getBrandName());
            service.getHistory().add(record);
            dog.lastInjectionTime = LocalTime.parse(time, TIME_FMT);
            service.save();
            refreshAll();
            showInfo("⚠️ 경고를 무시하고 투여 기록을 저장했습니다.");
        } catch (Exception ex) { showAlert("저장 중 오류가 발생했습니다."); }
    }

    private void showDangerAlert(String msg) {
        JOptionPane.showMessageDialog(this, msg, "🚨 위험 경고", JOptionPane.ERROR_MESSAGE);
    }

    private boolean showDangerConfirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "⚠️ 투여 경고",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ══════════════════════════════════════════════════════════════
    // 탭 2 · 투여 통계
    // ══════════════════════════════════════════════════════════════
    private void buildStats() {
        statsPanel.removeAll();
        statsPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        List<Object[]> rows = service.getStatsData();
        int totalCnt = 0; double totalDose = 0;
        for (Object[] r : rows) { totalCnt += (int)r[1]; totalDose += (double)r[2]; }

        // 요약 카드
        JPanel summary = new JPanel(new GridLayout(1, 2, 10, 0));
        summary.setOpaque(false);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        summary.add(statCard("이번 주 총 투여", totalCnt + " 회", service.getDogList().size() + "마리 합산"));
        summary.add(statCard("누적 총 투여량", String.format("%.1f u", totalDose), "전체 기록 기준"));
        statsPanel.add(summary);
        statsPanel.add(vgap(14));
        statsPanel.add(sectionLbl("반려견별 현황"));
        statsPanel.add(vgap(8));

        if (rows.isEmpty()) {
            statsPanel.add(emptyState("투여 기록이 없습니다", "인슐린 투여 후 통계가 표시됩니다."));
            refresh(statsPanel); return;
        }

        double maxDose = rows.stream().mapToDouble(r -> (double)r[2]).max().orElse(1);

        for (Object[] row : rows) {
            String name = (String)row[0]; int cnt = (int)row[1]; double dose = (double)row[2];

            JPanel dogRow = new JPanel();
            dogRow.setLayout(new BoxLayout(dogRow, BoxLayout.Y_AXIS));
            dogRow.setOpaque(false);
            dogRow.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0,BORDER_COL), new EmptyBorder(8,0,8,0)));
            dogRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            JLabel n = new JLabel(name);
            n.setFont(new Font("Dialog", Font.BOLD, 13));
            n.setForeground(TEXT_PRI);
            top.add(n, BorderLayout.WEST);
            top.add(badgeLbl(cnt + "회", BLUE_LIGHT, hex("#185fa5")), BorderLayout.EAST);

            JLabel sub = new JLabel(String.format("총 %.1f units", dose));
            sub.setFont(new Font("Dialog", Font.PLAIN, 11));
            sub.setForeground(TEXT_SEC);

            JProgressBar pb = new JProgressBar(0, 100);
            pb.setValue((int)(maxDose > 0 ? (dose / maxDose) * 100 : 0));
            pb.setForeground(BLUE_DARK);
            pb.setBackground(BORDER_COL);
            pb.setBorderPainted(false);
            pb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

            dogRow.add(top); dogRow.add(vgap(2)); dogRow.add(sub); dogRow.add(vgap(4)); dogRow.add(pb);
            statsPanel.add(dogRow);
        }
        refresh(statsPanel);
    }

    // ══════════════════════════════════════════════════════════════
    // 탭 3 · 투여 기록
    // ══════════════════════════════════════════════════════════════
    private void buildHist() {
        histPanel.removeAll();
        histPanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        List<String> hist = service.getHistory();

        // 헤더 행
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel totalLbl = new JLabel("총 " + hist.size() + "건");
        totalLbl.setFont(new Font("Dialog", Font.BOLD, 10));
        totalLbl.setForeground(BLUE_DARK);

        JButton clearBtn = smallBtn("🗑  전체 삭제", RED_TEXT, RED_LIGHT, hex("#f7c1c1"));
        clearBtn.addActionListener(e -> {
            if (showConfirm("모든 기록을 삭제하시겠습니까?")) {
                service.getHistory().clear(); service.save(); refreshAll();
            }
        });
        headerRow.add(totalLbl, BorderLayout.WEST);
        headerRow.add(clearBtn, BorderLayout.EAST);
        histPanel.add(headerRow);
        histPanel.add(vgap(8));

        if (hist.isEmpty()) {
            histPanel.add(emptyState("투여 기록이 없습니다", "인슐린을 투여하면 여기에 기록됩니다."));
            refresh(histPanel); return;
        }

        for (int i = hist.size() - 1; i >= 0; i--) {
            final int idx = i;
            String rec = hist.get(i);
            boolean isLong = rec.contains("캐닌슐린") || rec.contains("지속성");

            String dogName = "?", timeStr = "", doseStr = "";
            try { int ns = rec.indexOf("] ") + 2, ne = rec.indexOf(":", ns); dogName = rec.substring(ns, ne).trim(); } catch (Exception ignored) {}
            try { timeStr = rec.substring(rec.indexOf("[") + 1, rec.indexOf("]")); } catch (Exception ignored) {}
            try { int ds = rec.indexOf(": ") + 2, de = rec.indexOf(" units"); doseStr = rec.substring(ds, de) + " units"; } catch (Exception ignored) {}

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(BG_WHITE);
            row.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,BORDER_COL),
                new EmptyBorder(9,0,9,0)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel iconLbl = new JLabel(isLong ? "💧" : "⚡", SwingConstants.CENTER);
            iconLbl.setFont(new Font("Dialog", Font.PLAIN, 14));
            iconLbl.setOpaque(true);
            iconLbl.setBackground(isLong ? BLUE_LIGHT : AMBER_LIGHT);
            iconLbl.setPreferredSize(new Dimension(32, 32));
            iconLbl.setBorder(new EmptyBorder(4,4,4,4));

            JPanel txt = new JPanel();
            txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
            txt.setOpaque(false);
            JLabel nameLbl = new JLabel(dogName);
            nameLbl.setFont(new Font("Dialog", Font.BOLD, 13));
            nameLbl.setForeground(TEXT_PRI);
            JLabel timeLbl = new JLabel((isLong ? "지속성" : "속효성") + " · " + timeStr);
            timeLbl.setFont(new Font("Dialog", Font.PLAIN, 11));
            timeLbl.setForeground(TEXT_SEC);
            txt.add(nameLbl); txt.add(timeLbl);

            JPanel right = new JPanel();
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setOpaque(false);
            JLabel doseLbl = new JLabel(doseStr);
            doseLbl.setFont(new Font("Dialog", Font.BOLD, 13));
            doseLbl.setForeground(BLUE_DARK);
            JButton delBtn = smallBtn("삭제", TEXT_SEC, BG_SURFACE, BORDER_COL);
            delBtn.addActionListener(e -> { service.removeHistory(idx); refreshAll(); });
            right.add(doseLbl); right.add(Box.createVerticalStrut(4)); right.add(delBtn);

            row.add(iconLbl, BorderLayout.WEST);
            row.add(txt,     BorderLayout.CENTER);
            row.add(right,   BorderLayout.EAST);
            histPanel.add(row);
        }
        refresh(histPanel);
    }

    // ══════════════════════════════════════════════════════════════
    // 탭 4 · 반려견 관리
    // ══════════════════════════════════════════════════════════════
    private void buildManage() {
        managePanel.removeAll();
        managePanel.setBorder(new EmptyBorder(18, 18, 18, 18));
        managePanel.add(sectionLbl("등록된 반려견"));
        managePanel.add(vgap(8));

        List<Dog> dogs = service.getDogList();
        Color[] bgCols = { BLUE_LIGHT, GREEN_LIGHT, AMBER_LIGHT, hex("#fbeaf0"), hex("#ede9fe") };
        Color[] fgCols = { hex("#185fa5"), GREEN_TEXT, AMBER_TEXT, hex("#993556"), hex("#5b21b6") };

        if (dogs.isEmpty()) managePanel.add(emptyState("등록된 반려견이 없습니다", "아래 버튼으로 등록해 주세요."));

        for (int i = 0; i < dogs.size(); i++) {
            Dog dog = dogs.get(i);
            final int idx = i;
            int ci = i % bgCols.length;

            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,BORDER_COL),
                new EmptyBorder(10,0,10,0)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

            JLabel avatar = new JLabel(String.valueOf(dog.name.charAt(0)), SwingConstants.CENTER);
            avatar.setFont(new Font("Dialog", Font.BOLD, 14));
            avatar.setOpaque(true);
            avatar.setBackground(bgCols[ci]);
            avatar.setForeground(fgCols[ci]);
            avatar.setPreferredSize(new Dimension(36, 36));
            avatar.setBorder(new EmptyBorder(4,4,4,4));

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);
            String lastStr = dog.lastInjectionTime != null
                ? "마지막 투여: " + dog.lastInjectionTime.format(TIME_FMT)
                : "투여 기록 없음";
            JLabel nameLbl = new JLabel(dog.name);
            nameLbl.setFont(new Font("Dialog", Font.BOLD, 13));
            nameLbl.setForeground(TEXT_PRI);
            JLabel subLbl = new JLabel(dog.weight + " kg  ·  " + lastStr);
            subLbl.setFont(new Font("Dialog", Font.PLAIN, 11));
            subLbl.setForeground(TEXT_SEC);
            info.add(nameLbl); info.add(subLbl);

            JButton delBtn = smallBtn("삭제", RED_TEXT, RED_LIGHT, hex("#f7c1c1"));
            delBtn.addActionListener(e -> {
                if (showConfirm(dog.name + "을(를) 삭제하시겠습니까?")) {
                    if (selectedDog == dog) selectedDog = null;
                    service.removeDog(idx);
                    refreshAll();
                }
            });

            row.add(avatar, BorderLayout.WEST);
            row.add(info,   BorderLayout.CENTER);
            row.add(delBtn, BorderLayout.EAST);
            managePanel.add(row);
        }

        managePanel.add(vgap(12));

        JButton addBtn = new JButton("＋  새 반려견 등록");
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        addBtn.setFont(new Font("Dialog", Font.PLAIN, 13));
        addBtn.setForeground(TEXT_SEC);
        addBtn.setBackground(BG_SURFACE);
        addBtn.setBorder(new LineBorder(BORDER_COL, 1, true));
        addBtn.setOpaque(true); addBtn.setBorderPainted(true);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> showAddDogDialog());
        managePanel.add(addBtn);

        refresh(managePanel);
    }

    private void showAddDogDialog() {
        JTextField nf = styledField(""); nf.setPreferredSize(new Dimension(200, 30));
        JTextField wf = styledField(""); wf.setPreferredSize(new Dimension(200, 30));

        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.setBorder(new EmptyBorder(10, 0, 0, 0));
        p.add(new JLabel("이름")); p.add(nf);
        p.add(new JLabel("몸무게 (kg)")); p.add(wf);

        int result = JOptionPane.showConfirmDialog(this, p, "새 반려견 등록",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nf.getText().trim();
                double w = Double.parseDouble(wf.getText().trim());
                if (name.isEmpty()) { showAlert("이름을 입력해 주세요."); return; }
                service.addDog(name, w);
                refreshAll();
            } catch (Exception ex) { showAlert("몸무게는 숫자로 입력해 주세요."); }
        }
    }

    // ── 공통 UI 헬퍼 ─────────────────────────────────────────────
    private JLabel sectionLbl(String t) {
        JLabel l = new JLabel(t.toUpperCase());
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(BLUE_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return l;
    }

    private JPanel alertBar(String t) {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        b.setBackground(AMBER_LIGHT);
        b.setBorder(new LineBorder(hex("#ef9f27"), 1, true));
        JLabel l = new JLabel(t);
        l.setFont(new Font("Dialog", Font.PLAIN, 11));
        l.setForeground(AMBER_TEXT);
        b.add(l);
        return b;
    }

    private JPanel statCard(String title, String value, String sub) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_SURFACE);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            new EmptyBorder(14, 14, 14, 14)));
        JLabel tl = new JLabel(title); tl.setFont(new Font("Dialog", Font.PLAIN, 11)); tl.setForeground(TEXT_SEC);
        JLabel vl = new JLabel(value); vl.setFont(new Font("Dialog", Font.BOLD, 22)); vl.setForeground(TEXT_PRI);
        JLabel sl = new JLabel(sub);   sl.setFont(new Font("Dialog", Font.PLAIN, 11)); sl.setForeground(TEXT_SEC);
        card.add(tl); card.add(vl); card.add(sl);
        return card;
    }

    private JLabel badgeLbl(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Dialog", Font.PLAIN, 10));
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setBorder(new EmptyBorder(2, 7, 2, 7));
        return l;
    }

    private JButton smallBtn(String text, Color fg, Color bg, Color border) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Dialog", Font.PLAIN, 11));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(new LineBorder(border, 1, true));
        btn.setOpaque(true); btn.setBorderPainted(true); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTextField styledField(String text) {
        JTextField tf = new JTextField(text);
        tf.setFont(new Font("Dialog", Font.PLAIN, 13));
        tf.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            new EmptyBorder(7, 9, 7, 9)));
        tf.setBackground(BG_SURFACE);
        return tf;
    }

    private JLabel inputLbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Dialog", Font.PLAIN, 11));
        l.setForeground(TEXT_SEC);
        return l;
    }

    private void styleRadioBtn(JRadioButton btn) {
        btn.setOpaque(true);
        btn.setBackground(BG_WHITE);
        btn.setFont(new Font("Dialog", Font.PLAIN, 12));
        btn.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COL, 1, true),
            new EmptyBorder(10, 10, 10, 10)));
        btn.setFocusPainted(false);
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private Component vgap(int h) { return Box.createVerticalStrut(h); }

    // BoxLayout에서 최대 너비를 꽉 채우도록 강제
    private JComponent fixWidth(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    private JPanel emptyState(String t, String s) {
        JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS));
        b.setOpaque(false);
        b.setBorder(new EmptyBorder(40, 0, 40, 0));
        JLabel tl = new JLabel(t, SwingConstants.CENTER);
        tl.setFont(new Font("Dialog", Font.PLAIN, 14));
        tl.setForeground(TEXT_SEC);
        tl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sl = new JLabel(s, SwingConstants.CENTER);
        sl.setFont(new Font("Dialog", Font.PLAIN, 12));
        sl.setForeground(TEXT_SEC);
        sl.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.add(tl); b.add(Box.createVerticalStrut(6)); b.add(sl);
        return b;
    }

    private void refresh(JPanel p) { p.revalidate(); p.repaint(); }
    private void showAlert(String msg)  { JOptionPane.showMessageDialog(this, msg, "알림", JOptionPane.WARNING_MESSAGE); }
    private void showInfo(String msg)   { JOptionPane.showMessageDialog(this, msg, "완료", JOptionPane.INFORMATION_MESSAGE); }
    private boolean showConfirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "확인", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private static Color hex(String h) { return Color.decode(h); }
}