/*
 * RomRaider Open-Source Tuning, Logging and Reflashing
 * Copyright (C) 2006-2014 RomRaider.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.romraider.logger.ecu.definition;

import static com.romraider.logger.ecu.definition.xml.ConverterMaxMinDefaults.getDefault;
import static com.romraider.util.ByteUtil.asFloat;
import static com.romraider.util.ByteUtil.asSignedInt;
import static com.romraider.util.ByteUtil.asUnsignedInt;
import static com.romraider.util.JEPUtil.evaluate;
import static com.romraider.util.ParamChecker.checkNotNull;
import static com.romraider.util.ParamChecker.checkNotNullOrEmpty;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.romraider.logger.ecu.ui.handler.dash.GaugeMinMax;

public final class EcuParameterConvertorImpl implements EcuDataConvertor {
    private final String FLOAT = "float";
    private final String INT = "int";
    private final String units;
    private final String expression;
    private final DecimalFormat format;
    private final int bit;
    private final String dataType;
    private final Map<String, String> replaceMap;
    private final GaugeMinMax gaugeMinMax;

    public EcuParameterConvertorImpl() {
        this("Raw data", "x", "0", -1, "uint", new HashMap<String, String>(), getDefault());
    }

    public EcuParameterConvertorImpl(String units, String expression, String format, int bit, String dataType, Map<String, String> replaceMap,
                                     GaugeMinMax gaugeMinMax) {
        checkNotNullOrEmpty(units, "units");
        checkNotNullOrEmpty(expression, "expression");
        checkNotNullOrEmpty(format, "format");
        checkNotNull(replaceMap, "replaceMap");
        this.units = units;
        this.expression = expression;
        this.format = new DecimalFormat(format);
        this.bit = bit;
        this.dataType = (dataType == null ? "uint8" : dataType);
        this.replaceMap = replaceMap;
        this.gaugeMinMax = gaugeMinMax;
    }

    public double convert(byte[] bytes) {
        if (bit >= 0 && bit <= 31) {
            return (asUnsignedInt(bytes) & (1 << bit)) != 0 ? 1 : 0;
        } else {
            double value = 0;
            if (dataType.equalsIgnoreCase(FLOAT)) {
                value = (double) asFloat(bytes, 0 , bytes.length);
            }
            else if (dataType.toLowerCase().startsWith(INT)) {
                value = (double) asSignedInt(bytes);
            }
            else {
                value = (double) asUnsignedInt(bytes);
            }
            double result = evaluate(expression, value);
            return Double.isNaN(result) || Double.isInfinite(result) ? 0.0 : result;
        }
    }

    public String getUnits() {
        return units;
    }

    public GaugeMinMax getGaugeMinMax() {
        return gaugeMinMax;
    }

    public String getFormat() {
        return format.toPattern();
    }

    public String format(double value) {
        String formattedValue = format.format(value);
        if (replaceMap.containsKey(formattedValue)) {
            return replaceMap.get(formattedValue);
        } else {
            return formattedValue;
        }
    }

    public String toString() {
        return getUnits();
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String getDataType() {
        return dataType;
    }
}
