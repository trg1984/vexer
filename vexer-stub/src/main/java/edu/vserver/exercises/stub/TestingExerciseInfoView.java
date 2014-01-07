package edu.vserver.exercises.stub;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.vserver.exercises.model.ExerciseTypeDescriptor;
import edu.vserver.exercises.model.GeneralExerciseInfo;
import edu.vserver.standardutils.Localizer;

/**
 * A view imitating the view used in real ViLLE for showing info about an
 * exercise-instance.
 * 
 * @author Riku Haavisto
 * 
 */
class TestingExerciseInfoView extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7323519115445232073L;

	/* assignment icon */
	private final Embedded icon;

	/* fields & labels */
	private final Label titleLabel;

	public TestingExerciseInfoView(Localizer localizer,
			GeneralExerciseInfo exerInfo, ExerciseTypeDescriptor<?, ?> desc) {

		setStyleName("studentexercise-description-panel");
		setWidth("100%");

		setSpacing(true);
		setMargin(true);

		HorizontalLayout iconAndTitleLayout = new HorizontalLayout();
		iconAndTitleLayout.setSpacing(true);
		iconAndTitleLayout.setSizeUndefined();

		icon = new Embedded(null, (desc != null ? desc.getMediumTypeIcon()
				: TestStubDescription.INSTANCE.getIcon()));

		titleLabel = new Label((exerInfo != null ? exerInfo.getName()
				: TestStubDescription.INSTANCE.getName()));
		titleLabel.setStyleName("studentexercise-title");
		titleLabel.setSizeUndefined();

		iconAndTitleLayout.addComponent(icon);
		iconAndTitleLayout.setComponentAlignment(icon, Alignment.MIDDLE_LEFT);
		iconAndTitleLayout.addComponent(titleLabel);
		iconAndTitleLayout.setComponentAlignment(titleLabel,
				Alignment.MIDDLE_LEFT);

		addComponent(iconAndTitleLayout);
		setComponentAlignment(iconAndTitleLayout, Alignment.MIDDLE_LEFT);

		String descString = (exerInfo != null ? exerInfo.getDescription()
				: TestStubDescription.INSTANCE.getDescription());

		ExerciseDescriptionPanel exDescPanel = new ExerciseDescriptionPanel(
				localizer, descString);
		addComponent(exDescPanel);
		setComponentAlignment(exDescPanel, Alignment.MIDDLE_LEFT);

	}
}