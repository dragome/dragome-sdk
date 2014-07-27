/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

var flag = false;

$(document).ready(function()
{
	$(".toggle").click(function()
	{
		$(".panel").slideToggle("slide");
		$("#content").animate({
			height : '200px',
			bottom : '+=350'
		}, 1000)

		$(this).removeClass("collapsed expanded");

		if (flag)
			$(this).addClass('collapsed');
		else
			$(this).addClass('expanded');

		flag = !flag;
	});
});