/*
 * RHQ Management Platform
 * Copyright (C) 2005-2008 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation, and/or the GNU Lesser
 * General Public License, version 2.1, also as published by the Free
 * Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License and the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.rhq.core.domain.alert.composite;

import org.rhq.core.domain.alert.AlertCondition;
import org.rhq.core.domain.measurement.DataType;

/**
 * @author Joseph Marques
 */
public class AlertConditionBaselineCategoryComposite extends AlertConditionScheduleCategoryComposite {
    private final Integer baselineId;
    private final Double minValue;
    private final Double meanValue;
    private final Double maxValue;

    public AlertConditionBaselineCategoryComposite(AlertCondition condition, Integer scheduleId, Integer baselineId,
        Double minValue, Double meanValue, Double maxValue, DataType dataType) {
        super(condition, scheduleId, dataType);
        this.baselineId = baselineId;
        this.minValue = minValue;
        this.meanValue = meanValue;
        this.maxValue = maxValue;
    }

    public Integer getBaselineId() {
        return baselineId;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMeanValue() {
        return meanValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }
}
