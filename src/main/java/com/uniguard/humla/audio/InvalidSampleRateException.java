/*
 * Copyright (C) 2014 Andrew Comminos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.uniguard.humla.audio;

/**
 * Created by andrew on 23/04/14.
 */
@SuppressWarnings("serial")
 public class InvalidSampleRateException extends Exception {
    public InvalidSampleRateException(Exception e) {
        super(e);
    }

    public InvalidSampleRateException(String message) {
        super(message);
    }
}
