import { useState } from 'react';
import { ColorModeContext, useMode } from './theme';
import { Button, CssBaseline, ThemeProvider } from '@mui/material';
import { AnimatePresence } from "framer-motion";
import { Routes, Route, useLocation } from 'react-router-dom';
import SteamIdForm from "./scenes/steam-id-form";
import Sidebar from './scenes/global/Sidebar';
import Topbar from "./scenes/global/Topbar";
import News from './scenes/news';
import Profile from './scenes/profile';
import Friends from './scenes/friends';
import WeaponStats from "./scenes/weapon-stats";
import MapStats from "./scenes/map-stats";
import Bar from './scenes/bar';
import Pie from './scenes/pie';
import PlaytimeBooster from './scenes/playtime-booster';
import Calendar from './scenes/calendar';
import FAQ from './scenes/faq';
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
	const [theme, colorMode] = useMode();
	const location = useLocation();

	const [user, setUser] = useState(false);

	const userAccepted = () => setUser(true);
	const userDenied = () => setUser(null);

	return (
		<ColorModeContext.Provider value={colorMode}>
			<ThemeProvider theme={theme}>
				<CssBaseline/>
				<div className="app">
					{user ? (
						<>
							<Sidebar/>
							<main className="content">
								<Topbar/>
								<AnimatePresence mode='wait'>
									<Routes location={location} key={location.pathname}>
										<Route index exact path="/" element={<News/>}/>
										<Route exact path="/friends" element={<Friends/>}/>
										<Route exact path="/playtime-booster" element={<PlaytimeBooster/>}/>
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
								<Button onClick={userDenied} variant="contained">Sign Out</Button>
							</main>
						</>
					) : (
						<SteamIdForm userAcceptedFunction={userAccepted} userDeniedFunction={userDenied}/>
					)}
				</div>
			</ThemeProvider>
		</ColorModeContext.Provider>
	);
}

export default App;