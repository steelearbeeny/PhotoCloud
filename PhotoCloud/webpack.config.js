const path = require('path');
var webpack = require('webpack');
var WebpackBuildNotifierPlugin = require('webpack-build-notifier');
//const CopyPlugin = require('copy-webpack-plugin');
const WebpackShellPluginNext = require('webpack-shell-plugin-next');

//run: npx webpack

module.exports = {
  mode: 'development',
 
    entry: {
        App: './components/App.js'
        //UserAdmin: './components/useradmin/UserAdminPage.jsx',
        //Maintenance: './components/maintenance/SimpleMaintenanceForm.jsx'
    },

     watch: true,
   
    output: {
        path: path.resolve(__dirname, './src/main/webapp/js/'), //this is the windows path to write the output files
        publicPath: '/js/',                     //this is the URL path from the browser side where to find the file from outside
        //filename: '[name].[contenthash].js'
       filename: '[name].bundle.js'
    },
  
  plugins: [
        //new webpack.HotModuleReplacementPlugin(),
        new WebpackBuildNotifierPlugin({ 
			//notifyOptions: //{ timeout: 30 }
			
			      title: "PhotoCloud",
      //logo: path.resolve("./img/favicon.png"),
      suppressSuccess: false, // don't spam success notifications 
			}),
        new webpack.ProvidePlugin({
            'React': 'react',
            'ReactDOM':'react-dom/client'
        }),
        /*
        new CopyPlugin([
      		{ 
				from: path.resolve(__dirname, './src/main/webapp/js/'), 
      			to: path.resolve('/apache-tomcat-9.0.73/wtpwebapps/ScanViewer/js/') 
      		}
      
    		]),
    		*/
        //new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/)
        
        new WebpackShellPluginNext(
			{
				onBuildStart:
					{
						scripts: ['echo "Build Started"'],
						blocking: true,
						parallel: false
					}
				/*, 
				//note: dont need this step if running tomcat from project folders
				onAfterDone:
					{
						scripts: ['copy G:\\NewApp\\source\\PhotoCLoud\\src\\main\\webapp\\js\\App.bundle.js F:\\apache-tomcat-9.0.73\\sgawebapps\\PhotoCloud\\js',
								'echo "Build Complete"'
								],
						blocking: true,
						parallel: false
					}
					*/
			})
        
    ],
  
 
   module: {
        rules: [
		           {
		        		test: /\.(js|jsx)$/,
		        		exclude: /node_modules/,
		        		use: {
		          			loader: "babel-loader",
		          			options: {
		            			presets: ['@babel/preset-env']
		          			}
		        		}
		      		},
		      		          {
                test: /\.css$/,
                //use: {}
                //    loader: ['style-loader', 'css-loader']
                //}
                use: [
                    { loader: 'style-loader' },
                    { loader: 'css-loader' }

                ]

            },
            {
                test: /\.(png|jpg|gif)$/i,
                use: [
                    {
                        loader: 'url-loader'
                        
                    }
                ]

            }
            ]
            }
            
};