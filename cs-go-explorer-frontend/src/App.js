import { useState } from 'react';
import { ColorModeContext, useMode } from './theme';
import { CssBaseline, ThemeProvider } from '@mui/material';
import { AnimatePresence } from "framer-motion";
import { Routes, Route, useLocation, useNavigate } from 'react-router-dom';
import SteamIdForm from "./scenes/steam-id-form";
import Sidebar from './scenes/global/Sidebar';
import Topbar from "./scenes/global/Topbar";
import Profile from './scenes/profile';
import Friends from './scenes/friends';
import News from './scenes/news';
import Wallpapers from "./scenes/wallpapers";
import WeaponStats from "./scenes/weapon-stats";
import MapStats from "./scenes/map-stats";
import VideoPlatform from "./scenes/video-platform";
import Bar from "./scenes/bar";
import Pie from "./scenes/pie";
import Calendar from "./scenes/calendar";
import FAQ from "./scenes/faq";

function App() {
	const [theme, colorMode] = useMode();
	const [user, setUser] = useState(false);
	const location = useLocation();
	const navigate = useNavigate();

	const userAccepted = (userStatus) => {
		if (userStatus === "accept") {
			setUser(true);
			// navigate("/news");
			navigate("/profile");
		}
	}

	const userDenied = (userStatus) => {
		if (userStatus === "deny") {
			setUser(false);
			localStorage.clear();
			navigate("/");
		}
	}

	return (
		<ColorModeContext.Provider value={colorMode}>
			<ThemeProvider theme={theme}>
				<CssBaseline/>
				<div className="app">
					{user || localStorage.getItem("is_user_allowed") === "accept" ? (
						<>
							<Sidebar/>
							<main className="content">
								<Topbar userDenied={userDenied}/>
								<AnimatePresence mode='wait'>
									<Routes location={location} key={location.pathname}>
										<Route exact path="/profile" element={<Profile/>}/>
										<Route exact path="/friends" element={<Friends/>}/>
										<Route exact path="/news" element={<News/>}/>
										<Route exact path="/wallpapers" element={<Wallpapers/>}/>
										<Route exact path="/weapon-stats" element={<WeaponStats/>}>
											<Route exact path="bar" element={<Bar/>}/>
											<Route exact path="pie" element={<Pie/>}/>
										</Route>
										<Route exact path="/map-stats" element={<MapStats/>}>
											<Route exact path="bar" element={<Bar/>}/>
											<Route exact path="pie" element={<Pie/>}/>
										</Route>
										<Route exact path="/video-platform" element={<VideoPlatform/>}/>
										<Route exact path="/calendar" element={<Calendar/>}/>
										<Route exact path="/faq" element={<FAQ/>}/>
									</Routes>
								</AnimatePresence>
							</main>
						</>
					) : (
						<main className="content">
							<AnimatePresence mode='wait'>
								<SteamIdForm userAccepted={userAccepted} userDenied={userDenied}/>
							</AnimatePresence>
						</main>
					)}
				</div>
			</ThemeProvider>
		</ColorModeContext.Provider>
	);
}

export default App;