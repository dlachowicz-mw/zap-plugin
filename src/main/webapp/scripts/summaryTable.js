var clickedRow; 

function setDetailField(node, alrt)
{
	if (node.id != null &&
			node.id=="details_description") 
	{
		node.innerHTML = alrt.description;
		return true;
	}
	if (node.id != null &&
			node.id=="details_urls")
	{
		node.innerHTML = alrt.urls;
		return true;
	}
	if (node.id != null &&
			node.id=="details_solution") 
	{
		node.innerHTML = alrt.solution;
		return true;
	}
	if (node.id != null &&
			node.id=="details_other") 
	{
		node.innerHTML = alrt.otherInfo;
		return true;
	}
	if (node.id != null &&
			node.id=="details_reference") 
	{
		node.innerHTML = alrt.reference;
		return true;
	}
	return false
}

function setAlertDetails(node, alrt)
{				
	if ( !setDetailField(node, alrt) ) 
	{
		var i;
		for (i=0; i<node.childNodes.length; i++)
		{
			setAlertDetails(node.childNodes[i], alrt);
		}
	}
}

function displayAlert(row) 
{
	if (clickedRow!=null)
	{ 
		clickedRow.nextSibling.setAttribute("class", "display-off");
		if (clickedRow==row)
			{
				return;
			}
	}
			
	//result comes from form: var result = <st:bind value="${it.result}" />
	result.getAlert((row.rowIndex+1)/2, function(t) {

	clickedRow = row;
	detailRow =	row.nextSibling;
	var alrt = t.responseObject();
	detailRow.setAttribute("class", "display-on");
			
	setAlertDetails(detailRow, alrt);
	});
}