package com.dragome.forms.bindings.client.form.validation;

import com.dragome.model.interfaces.ValidationResult;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Aug 14, 2009
 * Time: 1:59:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HasValidationResult extends HasValidationHandlers
{
	ValidationResult getValidationResult();
}
