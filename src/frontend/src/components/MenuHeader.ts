import m = require('mithril');
import { Comp } from 'mithril';
import { User } from '../models/User';

function createNavbarEnd(): m.Children {
    if (User.current) {
        return m(".navbar-item.has-dropdown.is-hoverable", [
            m(".navbar-link", [
                m('span.icon', m('i.fa.fa-user-o')),
                User.current.name || 'My account',
            ]),
            m(".navbar-dropdown", [
                User.current.isUser()
                ? [
                    m("a.navbar-item[href='/dashboard']", { oncreate: m.route.link }, 'Dashboard'),
                    m("a.navbar-item[href='/profile']", { oncreate: m.route.link }, 'Profile'),
                    m("a.navbar-item[href='/settings']", { oncreate: m.route.link }, 'Settings'),
                    m('hr.navbar-divider'),
                ] : [],
                User.current.isAdmin()
                    ? [
                        m("a.navbar-item[href='/admin']", { oncreate: m.route.link }, 'Admin Dashboard'),
                        m('hr.navbar-divider'),
                    ] : [],
                m("a.navbar-item[href='/logout']", { oncreate: m.route.link }, 'Log out'),
            ]),
        ])
    } else {
        return m("a.navbar-item[href='/login']", { oncreate: m.route.link }, 'Login');
    }
}

export let MenuHeader = <Comp<{}, {}>>{

    oncreate: (vnode) => {
        document.addEventListener('DOMContentLoaded', () => {
            // Get all "navbar-burger" elements
            var $navbarBurgers = Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0);

            // Check if there are any navbar burgers
            if ($navbarBurgers.length > 0) {
                // Add a click event on each of them
                $navbarBurgers.forEach(function ($el) {
                    $el.addEventListener('click', function () {
                        // Get the target from the "data-target" attribute
                        var target = $el.dataset.target;
                        var $target = document.getElementById(target);

                        // Toggle the class on both the "navbar-burger" and the "navbar-menu"
                        $el.classList.toggle('is-active');
                        $target.classList.toggle('is-active');
                    });
                });
            }
        });
    },

    view: ({ attrs }) => {
        return m("nav.navbar[role='navigation'][aria-label='main navigation']", [
            m(".navbar-brand", [
                m("a.navbar-item[href='/']", { oncreate: m.route.link }, [
                    m(`img[src='${require('../assets/img/logo-68.png')}']`)
                ]),
                m("button.button.navbar-burger[data-target='navMain']", [
                    m('span'),
                    m('span'),
                    m('span'),
                ]),
            ]),
            m(".navbar-menu#navMain", [
                m(".navbar-start", [
                    m("a.navbar-item[href='javascript:void(0)']", 'Page1'),
                    m(".navbar-item.has-dropdown.is-hoverable", [
                        m(".navbar-link", 'Dropdown1'),
                        m(".navbar-dropdown", [
                            m("a.navbar-item[href='javascript:void(0)']", 'Page2'),
                            m("a.navbar-item[href='javascript:void(0)']", 'Page3'),
                        ]),
                    ]),
                ]),
                m(".navbar-end", createNavbarEnd()),
            ]),
        ]);
    },
};
