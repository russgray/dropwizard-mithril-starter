import * as m from 'mithril';
import { Comp } from 'mithril';

import { MenuHeader } from './components/MenuHeader';
import { Footer } from './components/Footer';

export let Page = <Comp<{}, {}>>{

    view: function (vnode) {
        return [
            m(MenuHeader, vnode),
            vnode.children,
            m(Footer, vnode),
        ];
    },
};

