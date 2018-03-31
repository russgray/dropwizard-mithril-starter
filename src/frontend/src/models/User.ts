import * as m from 'mithril';

import includes from 'lodash-es/includes';

import { request, getObjectFromStorage } from '../functions';

let bullet: any = require('bullet-pubsub');

interface NewUser {
    name: string;
    email?: string;
    phone?: string;
    address?: string;
    postcode?: string;
}

export class User {

    private static _current?: User;

    static get current(): User | undefined {
        if (!User._current) {
            let stored = getObjectFromStorage('user');
            if (stored) {
                User._current = new User(stored);
            }
        }

        return User._current;
    }

    userId: number;
    email?: string;
    name?: string;
    verified: boolean;
    roles: Role[];

    constructor(data: any) {
        this.userId = data.userId;
        this.email = data.email;
        this.name = data.nickname;
        this.verified = data.verified;
        this.roles = data.roles;
    }

    hasRole(role: Role) {
        return includes(this.roles, role);
    }

    isAdmin() {
        return this.hasRole("ADMIN");
    }

    isUser() {
        return this.hasRole("USER");
    }

    static LoadCurrentUser(): Promise<User> {

        let args: m.RequestOptions<{}> & { url: string } = {
            method: 'GET',
            url: '/auth/me',
            type: User,
        };

        return request(args)
            .then((user: User) => {
                User._current = user;
                bullet.trigger('login', user);
                return user;
            });
    }

    static Logout(): Promise<any> {
        let args: m.RequestOptions<{}> & { url: string } = {
            method: 'POST',
            url: '/auth/logout',
        };

        return request(args)
            .then(() => {
                User._current = undefined;
                bullet.trigger('logout');
            });
    }

    static CreateProspect(user: NewUser): Promise<any> {
        let args: m.RequestOptions<{}> & { url: string } = {
            method: 'POST',
            url: '/users/prospects',
            data: user
        };

        return request(args);
    }
};
