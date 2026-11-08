package org.delcom.maxwellpbo2026springbootcashflow.framework.view;

import org.delcom.maxwellpbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.maxwellpbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.maxwellpbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.maxwellpbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.maxwellpbo2026springbootcashflow.usecase.FinanceUseCase;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Tampilan web Vaadin aplikasi Cashflow.
 * View hanya berinteraksi dengan {@link FinanceUseCase} dan {@link FinancePresenter};
 * semua exception domain ditangkap di sini dan ditampilkan sebagai Notification.
 */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {
    private static final String EMPTY_MESSAGE = "- Data transaksi belum tersedia!";
    private static final String NOT_FOUND_MESSAGE = "- Transaksi tidak ditemukan!";

    private static final String INCOME_COLOR = "#16A34A";
    private static final String EXPENSE_COLOR = "#DC2626";

    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<SortOption> sortBox = new ComboBox<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span(EMPTY_MESSAGE);

    private final Span totalCount = new Span("0");
    private final Span totalIncome = new Span();
    private final Span totalExpense = new Span();
    private final Span balanceValue = new Span();

    public FinanceView(FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("Cashflow - Catatan Keuangan");
        configureGrid();
        configureToolbar();

        add(title, buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    // ------------------------------------------------------------------ ringkasan

    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                createSummaryCard("Total Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E40AF"),
                createSummaryCard("Total Pemasukan", totalIncome, "#DCFCE7", INCOME_COLOR, "#166534"),
                createSummaryCard("Total Pengeluaran", totalExpense, "#FEE2E2", EXPENSE_COLOR, "#991B1B"),
                createSummaryCard("Saldo", balanceValue, "#FEF3C7", "#D97706", "#92400E")
        );
        summary.setWidthFull();
        summary.setSpacing(true);
        summary.getStyle().set("align-items", "stretch");
        summary.getStyle().set("flex-wrap", "wrap");
        return summary;
    }

    private Div createSummaryCard(String label, Span value, String background, String accent, String valueColor) {
        Span caption = new Span(label);
        caption.getStyle()
                .set("display", "block")
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", accent);

        value.getStyle()
                .set("display", "block")
                .set("margin-top", "0.35rem")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("line-height", "1.2")
                .set("color", valueColor);

        Div card = new Div(caption, value);
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "200px")
                .set("background", background)
                .set("border", "1px solid " + accent)
                .set("border-left", "6px solid " + accent)
                .set("border-radius", "12px")
                .set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(financeUseCase.getAllTransactions().size()));
        totalIncome.setText(presenter.formatMoney(financeUseCase.getTotalIncome()));
        totalExpense.setText(presenter.formatMoney(financeUseCase.getTotalExpense()));
        balanceValue.setText(presenter.formatMoney(financeUseCase.getBalance()));
    }

    // ------------------------------------------------------------------ grid

    private void configureGrid() {
        grid.addColumn(Transaction::getId)
                .setHeader("ID")
                .setKey("id")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Transaction::getId);

        grid.addColumn(Transaction::getDescription)
                .setHeader("Keterangan")
                .setKey("description")
                .setFlexGrow(1)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));

        grid.addComponentColumn(this::createAmountLabel)
                .setHeader("Jumlah")
                .setKey("amount")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getAmount));

        grid.addComponentColumn(this::createTypeBadge)
                .setHeader("Tipe")
                .setKey("type")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getType));

        grid.addColumn(presenter::formatCreatedAt)
                .setHeader("Waktu")
                .setKey("createdAt")
                .setAutoWidth(true)
                .setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));

        grid.addComponentColumn(this::createActionButtons)
                .setHeader("Tindakan")
                .setKey("actions")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(false);

        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private Span createAmountLabel(Transaction transaction) {
        Span label = new Span(presenter.formatMoney(transaction.getAmount()));
        label.getStyle()
                .set("font-weight", "600")
                .set("color", transaction.isIncome() ? INCOME_COLOR : EXPENSE_COLOR);
        return label;
    }

    private Span createTypeBadge(Transaction transaction) {
        Span badge = new Span(presenter.typeLabel(transaction.getType()));
        String color = transaction.isIncome() ? INCOME_COLOR : EXPENSE_COLOR;
        badge.getStyle()
                .set("display", "inline-block")
                .set("padding", "0.1rem 0.6rem")
                .set("border-radius", "999px")
                .set("border", "1px solid " + color)
                .set("color", color)
                .set("font-size", "0.8rem")
                .set("font-weight", "600");
        return badge;
    }

    private HorizontalLayout createActionButtons(Transaction transaction) {
        Button deleteButton = new Button("Hapus", VaadinIcon.TRASH.create(),
                event -> openDeleteDialog(transaction));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(deleteButton);
    }

    // ------------------------------------------------------------------ toolbar

    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortBox.setPlaceholder("Urutkan...");
        sortBox.setItems(SortOption.values());
        sortBox.setItemLabelGenerator(presenter::sortLabel);
        sortBox.setClearButtonVisible(true);
        sortBox.addValueChangeListener(event -> applyFilters());
    }

    private HorizontalLayout buildToolbar() {
        Button incomeButton = new Button("Tambah Pemasukan", VaadinIcon.ARROW_CIRCLE_UP.create(),
                event -> openAddDialog(TransactionType.INCOME));
        incomeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button expenseButton = new Button("Tambah Pengeluaran", VaadinIcon.ARROW_CIRCLE_DOWN.create(),
                event -> openAddDialog(TransactionType.EXPENSE));
        expenseButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button searchButton = new Button("Cari", VaadinIcon.SEARCH.create(), event -> applyFilters());
        Button refreshButton = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), event -> refreshAll());

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField, searchButton, sortBox, refreshButton, incomeButton, expenseButton
        );
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        toolbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        toolbar.getStyle().set("flex-wrap", "wrap");
        return toolbar;
    }

    // ------------------------------------------------------------------ dialog

    private void openAddDialog(TransactionType type) {
        boolean income = type == TransactionType.INCOME;

        TextField descriptionField = new TextField("Keterangan");
        descriptionField.setWidthFull();
        descriptionField.setPlaceholder(income ? "mis. Gaji bulanan" : "mis. Belanja makanan");

        TextField amountField = new TextField("Jumlah (Rp)");
        amountField.setWidthFull();
        amountField.setPlaceholder("mis. 1000000");
        amountField.setPrefixComponent(new Span("Rp"));

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(income ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("480px");
        dialog.setMaxWidth("95vw");

        VerticalLayout form = new VerticalLayout(descriptionField, amountField);
        form.setPadding(false);
        dialog.add(form);

        Button saveButton = new Button("Simpan", VaadinIcon.CHECK.create(), event -> {
            try {
                BigDecimal amount = financeUseCase.parseAmount(amountField.getValue());
                Transaction created = income
                        ? financeUseCase.addIncome(descriptionField.getValue(), amount)
                        : financeUseCase.addExpense(descriptionField.getValue(), amount);
                showSuccess(income
                        ? presenter.showAddIncomeSuccess(created)
                        : presenter.showAddExpenseSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), event -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void openDeleteDialog(Transaction transaction) {
        Paragraph message = new Paragraph("Yakin ingin menghapus transaksi \""
                + transaction.getDescription() + "\" (ID " + transaction.getId() + ") senilai "
                + presenter.formatMoney(transaction.getAmount()) + "?");
        message.getStyle().set("margin", "0");

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Menghapus Transaksi");
        dialog.setWidth("480px");
        dialog.setMaxWidth("95vw");
        dialog.add(message);

        Button confirmButton = new Button("Hapus", VaadinIcon.TRASH.create(), event -> {
            deleteTransaction(transaction);
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancelButton = new Button("Batal", VaadinIcon.CLOSE.create(), event -> dialog.close());
        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    // ------------------------------------------------------------------ aksi

    private void deleteTransaction(Transaction transaction) {
        try {
            financeUseCase.removeTransaction(transaction.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
            refreshAll();
        } catch (ValidationException ex) {
            showError(presenter.showInvalidId());
        }
    }

    /** Menerapkan pencarian keterangan dan pengurutan SortOption secara bersamaan. */
    private void applyFilters() {
        List<Transaction> base;
        try {
            SortOption option = sortBox.getValue();
            base = option == null
                    ? financeUseCase.getAllTransactions()
                    : financeUseCase.sortTransactions(option);
        } catch (ValidationException ex) {
            showError(presenter.showInvalidSortOption());
            base = financeUseCase.getAllTransactions();
        }

        String keyword = searchField.getValue();
        if (keyword == null || keyword.isBlank()) {
            emptyState.setText(EMPTY_MESSAGE);
            refreshGrid(base, "Daftar Transaksi");
            return;
        }

        Set<Integer> matchedIds = financeUseCase.searchTransactions(keyword).stream()
                .map(Transaction::getId)
                .collect(Collectors.toSet());
        List<Transaction> results = base.stream()
                .filter(transaction -> matchedIds.contains(transaction.getId()))
                .toList();
        emptyState.setText(NOT_FOUND_MESSAGE);
        refreshGrid(results, "Hasil Pencarian: \"" + keyword.trim() + "\"");
    }

    private void refreshAll() {
        searchField.clear();
        sortBox.clear();
        emptyState.setText(EMPTY_MESSAGE);
        refreshGrid(financeUseCase.getAllTransactions(), "Daftar Transaksi");
    }

    private void refreshGrid(List<Transaction> transactions, String title) {
        grid.setItems(transactions);
        updateSummary();
        if (transactions.isEmpty()) {
            statusLabel.setText(title + " \u2014 " + emptyState.getText().replaceFirst("^- ", ""));
        } else {
            statusLabel.setText(title + " \u2014 " + presenter.showBalance(financeUseCase.getBalance()));
        }
    }

    private void showSuccess(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
