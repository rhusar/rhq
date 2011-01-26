/*
 * RHQ Management Platform
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.enterprise.gui.coregui.client.inventory.groups.detail.operation.history;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.rhq.core.domain.criteria.GroupOperationHistoryCriteria;
import org.rhq.core.domain.operation.GroupOperationHistory;
import org.rhq.core.domain.util.PageList;
import org.rhq.enterprise.gui.coregui.client.CoreGUI;
import org.rhq.enterprise.gui.coregui.client.inventory.common.detail.operation.history.OperationHistoryDataSource;

import java.util.Arrays;

/**
 * @author Ian Springer
 */
public class GroupOperationHistoryDataSource extends OperationHistoryDataSource<GroupOperationHistory> {

    public static abstract class Field extends OperationHistoryDataSource.Field {
        public static final String GROUP = "group";
    }

    public static abstract class CriteriaField {
        public static final String GROUP_ID = "groupId";
    }

    @Override
    protected void executeFetch(final DSRequest request, final DSResponse response) {
        GroupOperationHistoryCriteria criteria = new GroupOperationHistoryCriteria();

        if (request.getCriteria().getValues().containsKey(CriteriaField.GROUP_ID)) {
            int groupId = Integer.parseInt((String)request.getCriteria().getValues().get(
                CriteriaField.GROUP_ID));
            criteria.addFilterResourceGroupIds(Arrays.asList(groupId));
        }

        criteria.setPageControl(getPageControl(request));

        operationService.findGroupOperationHistoriesByCriteria(criteria,
                new AsyncCallback<PageList<GroupOperationHistory>>() {
                    public void onFailure(Throwable caught) {
                        CoreGUI.getErrorHandler().handleError(MSG.dataSource_operationHistory_error_fetchFailure(), caught);
                    }

                    public void onSuccess(PageList<GroupOperationHistory> result) {
                        response.setData(buildRecords(result));
                        processResponse(request.getRequestId(), response);
                    }
                });
    }

    @Override
    public ListGridRecord copyValues(GroupOperationHistory from) {
        ListGridRecord record = super.copyValues(from);
        record.setAttribute(Field.GROUP, from.getGroup());
        return record;
    }

}
