/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.ui.admin.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.p2.ui.admin.ProvAdminUIMessages;
import org.eclipse.equinox.p2.engine.Profile;
import org.eclipse.equinox.p2.ui.ProvUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * PropertyPage that shows an IU's properties
 * 
 * @since 3.4
 */
public class ProfilePropertyPage extends PropertyPage {

	private ProfileGroup profileGroup;

	protected Control createContents(Composite parent) {
		Profile profile = (Profile) ProvUI.getAdapter(getElement(), Profile.class);
		if (profile == null) {
			Label label = new Label(parent, SWT.DEFAULT);
			label.setText(ProvAdminUIMessages.No_Property_Item_Selected);
		}
		profileGroup = new ProfileGroup(parent, profile, new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				verifyComplete();
			}
		});
		Dialog.applyDialogFont(profileGroup.getComposite());
		verifyComplete();
		return profileGroup.getComposite();
	}

	void verifyComplete() {
		if (profileGroup == null) {
			return;
		}
		IStatus status = profileGroup.verify();
		setValid(status.isOK());
	}

	public boolean performOk() {
		profileGroup.updateProfile();
		return true;
	}
}
