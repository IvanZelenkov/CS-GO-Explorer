import { useState } from 'react';
import { ColorModeContext, useMode } from './theme';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { AnimatePresence } from "framer-motion";
import { Routes, Route } from 'react-router-dom';
import { useLocation } from "react-router-dom";
import Sidebar from './scenes/global/Sidebar';
import Dashboard from './scenes/dashboard';
import Topbar from "./scenes/global/Topbar";
import Friends from './scenes/friends';
import Contacts from './scenes/contacts';
import Profile from './scenes/profile';
import Calendar from './scenes/calendar';
import FAQ from './scenes/faq';
import MapStats from "./scenes/map-stats";
import WeaponStats from "./scenes/weapon-stats";
import Bar from './scenes/bar';
import Pie from './scenes/pie';

function App() {
	const [theme, colorMode] = useMode();
	const [isSidebar, setIsSidebar] = useState(true);
	const location = useLocation();

	return (
		<ColorModeContext.Provider value={colorMode}>
			<ThemeProvider theme={theme}>
				<CssBaseline/>
					<div className="app">
						<Sidebar isSidebar={isSidebar}/>
						<main className="content">
							<Topbar setIsSidebar={setIsSidebar}/>
							<AnimatePresence mode='wait'>
								<Routes location={location} key={location.pathname}>
									<Route exact path="/" element={<Dashboard/>}/>
									<Route exact path="/friends" element={<Friends/>}/>
									<Route exact path="/contacts" element={<Contacts/>}/>
									<Route exact path="/profile" element={<Profile/>}/>
									<Route exact path="/calendar" element={<Calendar/>}/>
									<Route exact path="/faq" element={<FAQ/>}/>
									<Route exact path="/weapon-stats" element={<WeaponStats/>}>
										<Route exact path="bar" element={<Bar/>}/>
										<Route exact path="pie" element={<Pie/>}/>
									</Route>
									<Route exact path="/map-stats" element={<MapStats/>}>
										<Route exact path="bar" element={<Bar/>}/>
										<Route exact path="pie" element={<Pie/>}/>
									</Route>
								</Routes>
							</AnimatePresence>
						</main>
					</div>
			</ThemeProvider>
		</ColorModeContext.Provider>
	);
}

export default App;