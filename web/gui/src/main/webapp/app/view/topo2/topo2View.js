/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 ONOS GUI -- Topology Layout Module.
 Module that contains the d3.force.layout logic
 */

(function () {
    'use strict';

    var dimensions;

    function newDim(_dimensions) {
        dimensions = _dimensions;
    }

    function getDimensions() {
        return dimensions;
    }

    angular.module('ovTopo2')
    .factory('Topo2ViewService',
        [
            function () {
                return {
                    newDim: newDim,
                    getDimensions: getDimensions
                }
            }
        ]
    );
})();