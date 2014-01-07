package edu.vserver.exercises.stub;

import java.util.HashSet;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

import edu.vserver.exercises.helpers.TestingExecutionSettings;
import edu.vserver.exercises.model.EditorHelper;
import edu.vserver.exercises.model.Executor;
import edu.vserver.exercises.model.ExerciseData;
import edu.vserver.exercises.model.ExerciseException;
import edu.vserver.exercises.model.ExerciseSaveListener;
import edu.vserver.exercises.model.ExerciseTypeDescriptor;
import edu.vserver.exercises.model.GeneralExerciseInfo;
import edu.vserver.standardutils.Localizer;
import edu.vserver.standardutils.StandardIcon;
import edu.vserver.standardutils.StandardUIConstants;
import edu.vserver.standardutils.StandardUIFactory;
import edu.vserver.standardutils.TempFilesManager;

/**
 * A quick and dirty class implementing {@link EditorHelper} that is crucial in
 * real ViLLE but only place-holder in the current stub-version.
 * 
 * @author Riku Haavisto
 * 
 */
class EditorHelperStubImpl<E extends ExerciseData> implements EditorHelper<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1533013485600936320L;

	private boolean editorWasShown;

	private final Localizer localizer;

	private EditedExerciseGiver<E> exerDataParser = null;

	private final ExerciseTypeDescriptor<E, ?> typeDesc;

	private final ControlButtons contLayout;

	private final TempFilesManager tempManager;

	private final EditorView editorView;

	private final HashSet<ExerciseSaveListener<E>> listeners = new HashSet<ExerciseSaveListener<E>>();

	public EditorHelperStubImpl(GeneralExerciseInfo exerInfo,
			Localizer localizer, ExerciseTypeDescriptor<E, ?> typeDesc,
			TempFilesManager tempManager) {
		this.localizer = localizer;
		this.typeDesc = typeDesc;
		this.tempManager = tempManager;
		this.editorView = new EditorView(exerInfo);

		this.contLayout = new ControlButtons();
	}

	@Override
	public GeneralExerciseInfo getExerciseInfo() {
		return new GeneralExerciseInfoStubImpl(editorView.getName(),
				editorView.getExerDescription());
	}

	@Override
	public Component getInfoEditorView() {
		return editorView;

	}

	@Override
	public TempFilesManager getTempManager() {
		return tempManager;
	}

	boolean wasShown() {
		return editorWasShown;
	}

	@Override
	public Layout getTestingExerciseView(Executor<?, ?> toTest) {
		return TestingExerciseView.getViewForTestingInEditor(toTest,
				getExerciseInfo(), localizer, tempManager);
	}

	@Override
	public void goBack() {
		UI.getCurrent().setContent(new StubStartView());
	}

	@Override
	public void setTestEnabled(boolean enabled) {
		contLayout.testButton.setEnabled(enabled);
	}

	@Override
	public void setSaveEnabled(boolean enabled) {
		contLayout.saveButton.setEnabled(enabled);
	}

	@Override
	public void registerExerSaveListener(ExerciseSaveListener<E> saveListener) {
		listeners.add(saveListener);
	}

	@Override
	public void informSaveListeners(E dataToSave) {
		for (ExerciseSaveListener<E> listener : listeners) {
			listener.actOnExerciseTypeSave(dataToSave);
		}
	}

	@Override
	public Component getControlbar(EditedExerciseGiver<E> toRegister) {
		exerDataParser = toRegister;
		return contLayout;
	}

	private class ControlButtons extends HorizontalLayout {

		/**
		 * 
		 */
		private static final long serialVersionUID = -966408303275424357L;
		private final Button saveButton;
		private final Button testButton;
		private final Button cancelButton;

		public ControlButtons() {

			setWidth("100%");
			setMargin(true);

			HorizontalLayout buttonLayout = new HorizontalLayout();
			buttonLayout.setSpacing(true);

			saveButton = StandardUIFactory.getDefaultButton(
					localizer.getUIText(StandardUIConstants.SAVE),
					StandardIcon.SAVE_ICON.getIcon());
			testButton = StandardUIFactory.getDefaultButton(
					localizer.getUIText(StandardUIConstants.TEST),
					StandardIcon.PREVIEW.getIcon());
			cancelButton = StandardUIFactory.getDefaultButton(
					localizer.getUIText(StandardUIConstants.CLOSE),
					StandardIcon.DELETE_ICON.getIcon());

			cancelButton.addClickListener(new Button.ClickListener() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -5183123395118709055L;

				@Override
				public void buttonClick(ClickEvent event) {
					goBack();
				}
			});

			saveButton.addClickListener(new Button.ClickListener() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1830860710406665375L;

				@Override
				public void buttonClick(ClickEvent event) {
					if (exerDataParser != null) {
						E data = exerDataParser.getCurrExerData(true);
						if (data != null) {

							informSaveListeners(data);
						}
					}
				}
			});

			testButton.addClickListener(new Button.ClickListener() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 5616753173844822725L;

				@Override
				public void buttonClick(ClickEvent event) {
					Executor<E, ?> execToUse = typeDesc.newExerciseExecutor();

					if (exerDataParser != null) {
						E data = exerDataParser.getCurrExerData(false);
						if (data != null) {
							try {
								execToUse.initialize(localizer, data, null,
										tempManager,
										TestingExecutionSettings.INSTANCE);
							} catch (ExerciseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								execToUse = null;
							}

							Component testView = getTestingExerciseView(execToUse);
							Window testWindow = new Window();
							testWindow.setModal(true);
							testWindow.setWidth("90%");
							testWindow.setHeight("90%");
							testWindow.addStyleName("opaque");
							testWindow.setContent(testView);
							testWindow.center();

							UI.getCurrent().addWindow(testWindow);
						}
					}
				}
			});

			buttonLayout.addComponent(saveButton);
			buttonLayout.addComponent(testButton);
			buttonLayout.addComponent(cancelButton);

			addComponent(buttonLayout);
			setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

		}
	}

	private class EditorView extends VerticalLayout implements ClickListener {

		private static final long serialVersionUID = 7155855298442571450L;

		private TextField exerciseName;

		private Label descriptionLabel;

		private RichTextArea description;

		private Label notesLabel;

		private TextArea notes;

		private Button hideShowButton;

		private boolean showDescriptions;

		private final boolean showNotes = true;

		public EditorView(GeneralExerciseInfo exerInfo) {
			String name = exerInfo.getName();
			String desc = exerInfo.getDescription();
			String notes = localizer
					.getUIText(StubUiConstants.NO_NOTES_FOR_TEACHERS_IN_STUB);
			doLayout("", name, desc, notes);
		}

		public void attach() {
			super.attach();
			editorWasShown = true;
		}

		private void doLayout(String caption, String name, String desc,
				String notesForTeachers) {

			setSpacing(true);

			this.setWidth("100%");
			showDescriptions = true;

			Panel p = new Panel(caption);
			p.setWidth("100%");
			p.addStyleName("bubble");
			p.addStyleName("info-panel-bolded");

			VerticalLayout pLayout = new VerticalLayout();
			pLayout.setMargin(true);
			pLayout.setSpacing(true);
			pLayout.setWidth("100%");

			exerciseName = new TextField();
			exerciseName.setWidth("100%");
			exerciseName.setValue(name);
			exerciseName.addStyleName("component-margin-bottom");
			pLayout.addComponent(new Label(localizer
					.getUIText(StubUiConstants.EXERCISE_NAME)));
			pLayout.addComponent(exerciseName);

			description = new RichTextArea();

			description.setWidth("100%");
			description.setHeight("200px");
			description.setValue(desc);
			description.addStyleName("component-margin-bottom");
			descriptionLabel = new Label(
					localizer.getUIText(StubUiConstants.DEFAULT_DESCRIPTION));
			pLayout.addComponent(descriptionLabel);
			pLayout.addComponent(description);

			notes = new TextArea();
			notes.setWidth("100%");
			notes.setRows(3);
			notes.setValue(notesForTeachers);
			notes.addStyleName("component-margin-bottom");
			notesLabel = new Label(
					localizer.getUIText(StubUiConstants.NOTES_FOR_TEACHERS));
			pLayout.addComponent(notesLabel);
			pLayout.addComponent(notes);

			hideShowButton = new Button(
					localizer.getUIText(StubUiConstants.HIDE_DESCRIPTIONS));
			hideShowButton.addClickListener(this);
			hideShowButton.setStyleName(BaseTheme.BUTTON_LINK);
			pLayout.addComponent(hideShowButton);

			p.setContent(pLayout);
			this.addComponent(p);

		}

		public String getName() {
			return ((String) exerciseName.getValue()).replaceAll("\\W", "");
		}

		public String getExerDescription() {
			return (String) description.getValue();
		}

		@Override
		public void buttonClick(ClickEvent event) {
			if (event.getSource() == hideShowButton) {
				hideShowDescriptions();
			}
		}

		private void hideShowDescriptions() {
			showDescriptions = !showDescriptions;

			descriptionLabel.setVisible(showDescriptions);
			description.setVisible(showDescriptions);
			if (showNotes) {
				notesLabel.setVisible(showDescriptions);
				notes.setVisible(showDescriptions);
			}

			if (showDescriptions) {
				hideShowButton.setCaption(localizer
						.getUIText(StubUiConstants.HIDE_DESCRIPTIONS));
			} else {
				hideShowButton.setCaption(localizer
						.getUIText(StubUiConstants.SHOW_DESCRIPTIONS));
			}

		}

	}

}
