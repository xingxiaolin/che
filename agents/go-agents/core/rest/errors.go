//
// Copyright (c) 2012-2018 Red Hat, Inc.
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// Contributors:
//   Red Hat, Inc. - initial API and implementation
//

package rest

import (
	"net/http"
)

// APIError represents http error
type APIError struct {
	error
	Code int
}

// BadRequest represents http error with 400 code
func BadRequest(err error) error {
	return APIError{err, http.StatusBadRequest}
}

// NotFound represents http error with code 404
func NotFound(err error) error {
	return APIError{err, http.StatusNotFound}
}

// Conflict represents http error with 409 code
func Conflict(err error) error {
	return APIError{err, http.StatusConflict}
}

// Forbidden represents http error with 403 code
func Forbidden(err error) error {
	return APIError{err, http.StatusForbidden}
}

// Unauthorized represents http error with 401 code
func Unauthorized(err error) error {
	return APIError{err, http.StatusUnauthorized}
}
