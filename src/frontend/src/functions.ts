import * as m from 'mithril';
import { RequestOptions } from 'mithril';

import includes from 'lodash-es/includes';

import * as storage from 'store';

import { User } from './models/User';

let bullet: any = require('bullet-pubsub');

const CSRF_METHODS: string[] = ['POST', 'PUT', 'DELETE', 'PATCH'];

interface ExtractFn {
    (xhr: XMLHttpRequest, options: any): string;
};

export function bindForm(data: object) {
    return {
        onchange: (e) => data[e.target.id] = e.target.value,
    };
};

export function getObjectFromStorage(name: string): any {
    let obj = storage.get(name);
    return obj ? JSON.parse(obj) : undefined;
};

export function whichAnimationEndEvent() {
    let el = document.createElement("fakeelement");

    let animations = {
        "animation": "animationend",
        "OAnimation": "oAnimationEnd",
        "MozAnimation": "animationend",
        "WebkitAnimation": "webkitAnimationEnd"
    }

    for (let t in animations) {
        if (el.style[t] !== undefined) {
            return animations[t];
        }
    }
}

export function request(xhrOptions: RequestOptions<{}> & { url: string }, next?: ExtractFn): Promise<any> {

    xhrOptions.config = (xhr) => {
        if (includes(CSRF_METHODS, xhrOptions.method)) {
            xhr.setRequestHeader('X-CSRF-Token', storage.get("CSRFToken"));
        }

        try {
            if (m.route.param('trace') === 'true') {
                xhr.setRequestHeader('X-Trace', 'true');
            }
        } catch (e) { /* ignore */ }
    };

    xhrOptions.extract = makeExtract(next);
    xhrOptions.withCredentials = true;
    return m.request(xhrOptions);
};

function makeExtract(next?: ExtractFn): ExtractFn {
    let safeNext = next || ((xhr, opts) => {
        try {
            return JSON.parse(xhr.responseText);
        } catch (e) {
            return {};
        }
    });

    return (xhr, opts) => {
        if (xhr.status === 401) {
            User.Logout();
        } else if (xhr.status >= 500) {
            bullet.trigger('httpErr', xhr.status);
        } else {
            bullet.trigger('xhrResponse', xhr);
            return safeNext(xhr, opts);
        }
    };
}