var webpack = require('webpack');
var path = require('path');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var CopyWebpackPlugin = require('copy-webpack-plugin');

var production = process.env.NODE_ENV === 'production';

var plugins = [
    new HtmlWebpackPlugin({
        template: './frontend/templates/index.pug',
        excludeChunks: ['Test']
    }),

    new CopyWebpackPlugin([{
        from: './frontend/error_html',
        to: 'ErrorPages/'
    }]),

    // https://github.com/petehunt/webpack-howto
    new webpack.DefinePlugin({
        __ENV__: JSON.stringify(production ? 'production' : 'development'),

        // DEBUG enabled if BUILD_DEBUG is set, or we're not a production build
        __DEBUG__: JSON.stringify(JSON.parse(process.env.BUILD_DEBUG || (production ? 'false' : 'true'))),

        __PRERELEASE__: JSON.stringify(JSON.parse(process.env.BUILD_PRERELEASE || 'false')),

        // Rollbar enabled if ROLLBAR_ENABLED is set, or we're in production
        __ROLLBAR_ENABLED__: JSON.stringify(process.env.ROLLBAR_ENABLED || (production ? true : false)),

        // If not in production, set rollbar to log to console
        __ROLLBAR_VERBOSE__: JSON.stringify(!production),
    }),
];

if (production) {
    plugins = plugins.concat([
        // Add minification (use plugin rather than webpack.optimize.UglifyJsPlugin in order to correctly
        // handle js files such as rollbar_init, see https://stackoverflow.com/a/43674989)
        new webpack.optimize.UglifyJsPlugin({
            mangle: true,
            compress: {
                warnings: false,
            },
            exclude: [
                // './frontend/vendor/js/rollbar_init.js'
            ]
        }),
    ]);
}

module.exports = {
    entry: {
        App: './frontend/src/app.ts',
        Test: './frontend/tests/Spec.ts'
    },
    output: {
        path: __dirname + '/backend/src/main/resources/assets',
        filename: production ? 'js/[name]-[hash].js' : 'js/[name]-bundle.js'
    },
    externals: {
    },
    devtool: 'source-map',
    resolve: {
        extensions: ['.webpack.js', '.web.js', '.js', '.jade', '.scss', '.css', '.ts', '.png']
    },
    plugins: plugins,
    module: {
        rules: [{
            test: /\.ts$/,
            use: [{
                loader: 'ts-loader'
            }]
        }, {
            test: /\.pug$/,
            use: [{
                loader: 'pug-loader'
            }]
        }, {
            test: /\.(css|scss)$/,
            use: [
                'style-loader',
                'css-loader',
                'sass-loader'
            ]
        }, {
            test: /\.(png|gif|jpe?g|svg)$/i,
            use: [{
                loader: 'url-loader',
                options: {
                    limit: 8192
                }
            }]
        }]
    },
};